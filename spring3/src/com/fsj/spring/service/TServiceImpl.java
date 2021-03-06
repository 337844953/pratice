package com.fsj.spring.service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fsj.spring.dao.BaseDao;
import com.fsj.spring.model.sys.SysUser;
import com.fsj.spring.util.ConversionUtils;
/**
 * Title:通用服务层实现类
 *
 * @author 唐有欢
 * @version 1.0 , 2013-1-18 创建
 */
public class TServiceImpl implements TService {
	private static final Logger log = LoggerFactory.getLogger(TServiceImpl.class);

	protected BaseDao baseDao;

	protected SysUser sysUser;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public void setLoginUser(SysUser user) {
		this.sysUser = user;
	}

	public void saveOrUpdate(Object o) {
		Object[] objs = new Object[] { o };
		setObjectSaveValue(objs);
		baseDao.saveOrUpdate(objs[0]);
	}

	public void deleteAllObjects(Collection<?> collection) {
		baseDao.deleteAll(collection);

	}

	public void deleteObject(Object instance) {
		baseDao.delete(instance);
	}

	public void deleteAllObjects(Class clazz, List<?> uids) {
		if (uids != null && uids.size() > 0) {
			for (Iterator iter = uids.iterator(); iter.hasNext();) {
				deleteObject(baseDao.findById(clazz, (Serializable) iter.next()));
			}
		}
	}

	public Object getObjectById(Object o, Serializable id) {
		return baseDao.findById(o.getClass(), id);
	}

	public void setObjectSaveValue(Object[] o) {

		Method setMethod = null;
		Method getMethod = null;
		Object result = null;
		Object id = null;
		Object updateObject = null;

		// 1、判断是否是更新操作 ， 如果是更新：复制对象属性值到数据库对象中
		try {
			getMethod = o[0].getClass().getMethod("getId", null);
			id = getMethod.invoke(o[0], null);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if (id != null) {
			updateObject = baseDao.findById(o[0].getClass(), (Serializable) id);
			if (updateObject != null) {
				try {
					ConversionUtils.convertAttribute(updateObject, o[0], ConversionUtils.GOAL);
					o[0] = updateObject;
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
		try {
			getMethod = o[0].getClass().getMethod("getCrtC", null);
			result = getMethod.invoke(o[0], null);
			if (result == null) {
				setMethod = o[0].getClass().getMethod("setCrtC", new Class[] { String.class });
				setMethod.invoke(o[0], sysUser.getSuUsername());
			}
			result = null;

			getMethod = o[0].getClass().getMethod("getCrtDate", null);
			result = getMethod.invoke(o[0], null);
			if (result == null) {
				setMethod = o[0].getClass().getMethod("setCrtDate", new Class[] { Date.class });
				setMethod.invoke(o[0], new Date());
			}
			result = null;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
