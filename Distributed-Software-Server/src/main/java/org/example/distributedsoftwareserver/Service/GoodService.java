package org.example.distributedsoftwareserver.Service;

import com.google.common.annotations.Beta;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.distributedsoftwareserver.Common.Result;
import org.example.distributedsoftwareserver.Entity.DTO.CreateGoodDTO;
import org.example.distributedsoftwareserver.Entity.Model.Good;
import org.example.distributedsoftwareserver.Entity.VO.CheckGoodVO;
import org.example.distributedsoftwareserver.Mapper.GoodMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Component
public class GoodService {
    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private BloomFilter<Long> goodBloomFilter;

    @PostConstruct
    public void init() {
        // Assume 1,000,000 expected insertions and 0.01 false positive probability
        goodBloomFilter = BloomFilter.create(Funnels.longFunnel(), 1000000, 0.01);

        // In a real project, you should load all existing goodIds from DB into the Bloom filter here
        try {
            List<Good> goods = goodMapper.selectAllGoods(100); // Sample loading
            if (goods != null) {
                for (Good g : goods) {
                    goodBloomFilter.put(g.getGoodId());
                }
                log.info("Bloom Filter initialized with {} goods", goods.size());
            }
        } catch (Exception e) {
            log.error("Failed to initialize Bloom Filter", e);
        }
    }

    @Value("${server.port}")
    private String port;

    public Result getGoodList(HttpServletRequest request) {
        List<Good> allGoods = new ArrayList<>();
        try{
            allGoods = goodMapper.selectAllGoods(100);
            log.info("Get Goods List Successfully, Count: {}", allGoods.size());
        }catch (Exception e){
            log.error("Get Goods List Unsuccessfully, Error Message: {}", e.getMessage());
            return Result.error("获取商品列表失败，请稍后再试！");
        }

        List<List<Good>> pages = new ArrayList<>();
        int pageSize = 5;
        int totalGoods = allGoods.size();

        for(int i = 0; i < totalGoods; i += pageSize) {
            int end = Math.min(i + pageSize, totalGoods);
            pages.add(new ArrayList<>(allGoods.subList(i, end)));
        }

        return Result.success(pages);
    }

    public Result getGoodByID(Long goodId, HttpServletRequest request) {
        if (!goodBloomFilter.mightContain(goodId)) {
            log.warn("Cache Penetration detected! GoodID {} not in Bloom Filter", goodId);
            return Result.error("未找到该商品，请检查商品ID！");
        }
        log.info("Get GoodByID from BloomFilter successfully");

        String cacheKey = "good:" + goodId;
        String lockKey = "lock:good:" + goodId;

        try {
            CheckGoodVO cachedGood = (CheckGoodVO) redisTemplate.opsForValue().get(cacheKey);
            if (cachedGood != null) {
                log.info("Get Good From Redis, GoodID: {}", goodId);
                return Result.success(cachedGood);
            }
        } catch (Exception e) {
            log.error("Failed to get good detail from Redis, goodId: {}, error: {}", goodId, e.getMessage());
        }

        // Redisson锁实现分布式锁，防止缓存击穿
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    CheckGoodVO doubleCheck = (CheckGoodVO) redisTemplate.opsForValue().get(cacheKey);
                    if (doubleCheck != null) {
                        return Result.success(doubleCheck);
                    }

                    Good good = goodMapper.selectGoodByID(goodId);
                    if (good == null) {
                        log.warn("Good not found in DB, goodId: {}", goodId);
                        return Result.error("未找到该商品！");
                    }

                    log.info("Successfully fetched good detail from database, goodId: {}", goodId);
                    CheckGoodVO checkGoodVO = new CheckGoodVO();
                    BeanUtils.copyProperties(good, checkGoodVO);

                    // 设置随机过期时间，防止缓存雪崩
                    int randomMinutes = 60 + new Random().nextInt(10);
                    redisTemplate.opsForValue().set(cacheKey, checkGoodVO, randomMinutes, TimeUnit.MINUTES);
                    log.info("Stored good detail in Redis with {} min TTL, goodId: {}", randomMinutes, goodId);

                    return Result.success(checkGoodVO);
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("Failed to acquire lock for goodId: {}, possible high concurrency", goodId);
                return Result.error("服务器繁忙，请稍后再试！");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted, goodId: {}", goodId);
            return Result.error("系统繁忙");
        } catch (Exception e) {
            log.error("Error during fetching good detail with lock, goodId: {}", goodId, e);
            return Result.error("获取商品详情失败");
        }
    }

    public Result createGood(CreateGoodDTO createGoodDTO, HttpServletRequest request) {
        if(createGoodDTO.getGoodName() == null || createGoodDTO.getGoodName().isEmpty()) {
            return Result.error("商品名称不能为空，请重新输入！");
        }

        if(createGoodDTO.getGoodDescription() == null || createGoodDTO.getGoodDescription().isEmpty()) {
            createGoodDTO.setGoodDescription("暂无描述");
        }

        if(createGoodDTO.getGoodPrice() == null || createGoodDTO.getGoodPrice() < 0) {
            return Result.error("商品价格不能为空且必须为非负数，请重新输入！");
        }

        if(createGoodDTO.getGoodInventory() == null || createGoodDTO.getGoodInventory() < 0) {
            return Result.error("商品库存不能为空且必须为非负数，请重新输入！");
        }

        Good newGood = new Good();
        BeanUtils.copyProperties(createGoodDTO, newGood);

        try{
            goodMapper.insertGood(newGood);
            if (newGood.getGoodId() != null) {
                goodBloomFilter.put(newGood.getGoodId());
                log.info("Added new goodId {} to Bloom Filter", newGood.getGoodId());
            }
            log.info("Successfully created good, goodName: {}", newGood.getGoodName());
        }catch (Exception e){
            log.error("Failed to create good, goodName: {}, error: {}", newGood.getGoodName(), e.getMessage());
            return Result.error("创建商品失败，请稍后再试！");
        }

        return Result.success("商品创建成功！");
    }
}
