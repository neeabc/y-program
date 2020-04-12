package com.offcn.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.mapper.TbSeckillOrderMapper;
import com.offcn.pojo.TbSeckillGoods;
import com.offcn.pojo.TbSeckillOrder;
import com.offcn.pojo.TbSeckillOrderExample;
import com.offcn.pojo.TbSeckillOrderExample.Criteria;
import com.offcn.seckill.service.SeckillOrderService;
import com.offcn.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckill_orderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckill_orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckill_orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckill_order) {
		seckill_orderMapper.insert(seckill_order);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckill_order){
		seckill_orderMapper.updateByPrimaryKey(seckill_order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckill_orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckill_orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckill_order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckill_order!=null){			
						if(seckill_order.getUserId()!=null && seckill_order.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckill_order.getUserId()+"%");
			}			if(seckill_order.getSellerId()!=null && seckill_order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckill_order.getSellerId()+"%");
			}			if(seckill_order.getStatus()!=null && seckill_order.getStatus().length()>0){
				criteria.andStatusLike("%"+seckill_order.getStatus()+"%");
			}			if(seckill_order.getReceiverAddress()!=null && seckill_order.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckill_order.getReceiverAddress()+"%");
			}			if(seckill_order.getReceiverMobile()!=null && seckill_order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckill_order.getReceiverMobile()+"%");
			}			if(seckill_order.getReceiver()!=null && seckill_order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckill_order.getReceiver()+"%");
			}			if(seckill_order.getTransactionId()!=null && seckill_order.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckill_order.getTransactionId()+"%");
			}	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckill_orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

//	@Autowired
//	private TbSeckillOrderMapper seckill_orderMapper;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private TbSeckillGoodsMapper seckill_goodsMapper;

//	@Override
//	public void submitOrder(Long seckillId, String userId) {
//
//		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
//		if (seckillGoods == null) {
//			throw new RuntimeException("商品不存在");
//		}
//		if (seckillGoods.getStockCount() <= 0) {
//			throw new RuntimeException("商品已被抢购一空");
//		}
//		// 扣减库存, 若被秒光则同步到数据库
//		seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
//		redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);
//		if (seckillGoods.getStockCount() == 0) {
//			// 若被秒光则同步到数据库
//			seckill_goodsMapper.updateByPrimaryKey(seckillGoods);
//			redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
//		}
//		// 保存订单到redis中
//		long orderId = idWorker.nextId();
//		TbSeckillOrder seckillOrder = new TbSeckillOrder();
//		seckillOrder.setId(orderId);
//		seckillOrder.setCreateTime(new Date());
//		seckillOrder.setMoney(seckillGoods.getCostPrice());
//		seckillOrder.setSeckillId(seckillId);
//		seckillOrder.setSellerId(seckillGoods.getSellerId());
//		seckillOrder.setUserId(userId);
//		seckillOrder.setStatus("0");
//
//		redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
//
//	}

	@Override
	public void submitOrder(Long seckillId, String userId) {

		// 开启redis事务
		redisTemplate.setEnableTransactionSupport(true);
		Object result = redisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			public List<Object> execute(RedisOperations redisOperations) throws DataAccessException {

				/*
					WATCH命令可以为redis事务提供"check-and-set" cas行为,
					被WATCH的键会被监听,并发觉是否被改动过,
					若至少一个被监视的键在exec执行之前被修改过, 那么整个事务都会被取消,操作会被回滚
					exec返回空, 多条批量回复(null multi-bulk reply) 来表示事务失败
				 */
				redisOperations.watch("seckillGoods");
				TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);

				// 开启事务
				redisOperations.multi();
				redisOperations.boundHashOps("seckillGoods").get(seckillId);// 必要的空查询
				if (seckillGoods == null) {
					redisOperations.exec();
					throw new RuntimeException("商品不存在");
				}
				if (seckillGoods.getStockCount() <= 0) {
					redisOperations.exec();
					throw new RuntimeException("商品已被抢购一空");
				}

				// 扣减库存
				seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
				redisOperations.boundHashOps("seckillGoods").put(seckillId, seckillGoods);

				if (seckillGoods.getStockCount() == 0) {
					// 若被秒光则同步到数据库
					seckill_goodsMapper.updateByPrimaryKey(seckillGoods);
					redisOperations.boundHashOps("seckillGoods").delete(seckillId);
				}
				// 保存订单到redis中
				long orderId = idWorker.nextId();
				TbSeckillOrder seckillOrder = new TbSeckillOrder();
				seckillOrder.setId(orderId);
				seckillOrder.setCreateTime(new Date());
				seckillOrder.setMoney(seckillGoods.getCostPrice());
				seckillOrder.setSeckillId(seckillId);
				seckillOrder.setSellerId(seckillGoods.getSellerId());
				seckillOrder.setUserId(userId);
				seckillOrder.setStatus("0");

				redisOperations.boundHashOps("seckillOrder").put(userId, seckillOrder);

				// 提交事务
				return redisOperations.exec();
			}
		});
	}

	@Override
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	@Override
	public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
		System.out.println("saveOrderFromRedisToDb:" + userId);
		// 根据用户id查询日志
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if (seckillOrder == null) {
			throw new RuntimeException("订单不存在");
		}
		// 若与传递的订单号不符
		if (seckillOrder.getId().longValue() != orderId.longValue()) {
			throw new RuntimeException("订单不符合");
		}
		seckillOrder.setTransactionId(transactionId);// 交易流水号
		seckillOrder.setPayTime(new Date());
		seckillOrder.setStatus("1");
		seckill_orderMapper.insert(seckillOrder);
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
	}

	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if (seckillOrder != null && seckillOrder.getId().longValue() == orderId.longValue()) {
			// 删除缓存订单并恢复库存
			redisTemplate.boundHashOps("seckillOrder").delete(userId);
			TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
			if (seckillGoods != null) {
				seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
				redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);// 放入缓存
			}
		}

	}


}
