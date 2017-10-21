package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.tools.doclets.internal.toolkit.Content;
import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;


@Service
public class ContentCatagoryServiceImpl implements ContentCategoryService{

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		// 根据parentId查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> resultList = new ArrayList<>();
		
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			
			resultList.add(node);
		}
		return resultList;
		
	}

	/**
	 * 前端添加分类目录或者叶子目录，传入后台为父类id和目录名字
	 * 若添加之前的为叶子目录，则修改其为父目录
	 */
	
	@Override
	public TaotaoResult addContentCategory(Long parentId, String name) {
		// 创建一个pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		// 补全对象属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		// 状态1正常 2删除
		contentCategory.setStatus(1);
		// 排序 默认为1
		contentCategory.setSortOrder(1);
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		// 插入到数据库
		contentCategoryMapper.insert(contentCategory);
		
		// 判断父节点的状态
		
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parent.getIsParent()) {
			// 如果父节点为叶子节点 改为父节点
			parent.setIsParent(true);
			// 数据库进行更新父类节点状态
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		
		// 返回结果
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult updateContentCategory(Long id, String name) {
		// 通过id查询要修改的对象
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		// 进行设置name属性
		contentCategory.setName(name);
		// 更新数据库
		contentCategoryMapper.updateByPrimaryKey(contentCategory);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult deleteContentCategory(Long id) {
		
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		// 判断当前有没有子类目录
		int num = contentCategoryMapper.getChildrenNum(contentCategory);
		if (num!=0)
			throw new RuntimeException("父类目录下有子目录文件，不能删除");
		
		int deleteByPrimaryKey = contentCategoryMapper.deleteByPrimaryKey(id);
		if(deleteByPrimaryKey!=1)
			throw new RuntimeException("删除失败");
		
		return TaotaoResult.ok();
	}

}
