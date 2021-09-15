package com.xtyu.toolapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xtyu.toolapi.exception.Asserts;
import com.xtyu.toolapi.exception.WxInfoException;
import com.xtyu.toolapi.mapper.WxUserMapper;
import com.xtyu.toolapi.model.entity.WxUser;
import com.xtyu.toolapi.service.WxUserService;
import com.xtyu.toolapi.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: 小熊
 * @date: 2021/6/15 13:38
 * @description:phone 17521111022
 */
@Service("wxUserService")
public class WxUserServiceImpl implements WxUserService {

    @Resource
    private WxUserMapper wxUserMapper;

    @Override
    public WxUser getUserInfoByOpenId(String openId) {
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openId);
        List<WxUser> userList = wxUserMapper.selectList(queryWrapper);
        if (!userList.isEmpty())
            return userList.get(0);
        return null;
    }

    @Override
    public int insert(WxUser wxUser) {
        return wxUserMapper.insert(wxUser);
    }

    @Override
    public int updateById(WxUser wxUser) {
        return wxUserMapper.updateById(wxUser);
    }

    @Override
    public WxUser singIn(String openId) {
        WxUser wxUser = getUserInfoByOpenId(openId);
        if (wxUser==null)
            Asserts.wxInfoFail("获取用户信息失败");
        Date signTime = wxUser.getEndSignInTime();//最后签到时间
        Date nowDate = new Date();
        Date startDate = DateUtil.getStartTime(nowDate);//今日开始时间
        Date endDate = DateUtil.getEndTime(nowDate);//今日结束时间
        if (signTime==null||signTime.before(startDate)||signTime.after(endDate)){
            wxUser.setSignInSum(wxUser.getSignInSum()+1);
            wxUser.setVideoNumber(wxUser.getVideoNumber()+20);
            wxUser.setEndSignInTime(nowDate);
            wxUserMapper.updateById(wxUser);
        }else {
            Asserts.wxInfoFail("重复签到");
        }
        return wxUser;
    }
}
