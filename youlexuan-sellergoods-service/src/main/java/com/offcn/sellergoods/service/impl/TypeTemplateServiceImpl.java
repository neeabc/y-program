package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.mapper.TbTypeTemplateMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.pojo.TbTypeTemplate;
import com.offcn.pojo.TbTypeTemplateExample;
import com.offcn.pojo.TbTypeTemplateExample.Criteria;
import com.offcn.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper type_templateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return type_templateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) type_templateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate type_template) {
		type_templateMapper.insert(type_template);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate type_template){
		type_templateMapper.updateByPrimaryKey(type_template);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return type_templateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			type_templateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate type_template, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(type_template!=null){			
						if(type_template.getName()!=null && type_template.getName().length()>0){
				criteria.andNameLike("%"+type_template.getName()+"%");
			}			if(type_template.getSpecIds()!=null && type_template.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+type_template.getSpecIds()+"%");
			}			if(type_template.getBrandIds()!=null && type_template.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+type_template.getBrandIds()+"%");
			}			if(type_template.getCustomAttributeItems()!=null && type_template.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+type_template.getCustomAttributeItems()+"%");
			}	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)type_templateMapper.selectByExample(example);

		saveToRedis();//存入数据到缓存
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return type_templateMapper.selectOptionList();
	}

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	@Override
	public List<Map> findSpecList(Long id) {
		//查询模板
		TbTypeTemplate typeTemplate = type_templateMapper.selectByPrimaryKey(id);

		List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class)  ;
		for(Map map:list){
			//查询规格选项列表
			TbSpecificationOptionExample example=new TbSpecificationOptionExample();
			com.offcn.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo( new Long( (Integer)map.get("id") ) );
			List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
			map.put("options", options);
		}
		return list;
	}


	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 将数据存入缓存
	 */
	private void saveToRedis(){
		//获取模板数据
		List<TbTypeTemplate> typeTemplateList = findAll();
		//循环模板
		for(TbTypeTemplate typeTemplate :typeTemplateList){
			//存储品牌列表
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
			//存储规格列表
			List<Map> specList = findSpecList(typeTemplate.getId());//根据模板ID查询规格列表
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
		}
	}
}
