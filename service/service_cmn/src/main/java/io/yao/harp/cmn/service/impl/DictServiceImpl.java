package io.yao.harp.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import io.yao.harp.cmn.listener.DictListener;
import io.yao.harp.cmn.mapper.DictMapper;
import io.yao.harp.cmn.service.DictService;
import io.yao.harp.model.cmn.Dict;
import io.yao.harp.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private  DictMapper dictMapper;

    @Override
    public List<Dict> findChildrenData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> dicts = baseMapper.selectList(wrapper);
        for(Dict dict : dicts) {
            Long dictId = dict.getId();
            boolean hasChild = hasChildData(dictId);
            dict.setHasChildren(hasChild);
        }
        return dicts;
    }

    @Override
    public void exportDictData(HttpServletResponse response) {
        // set download information
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String filename = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".xlsx");

        // search database
        List<Dict> dicts = baseMapper.selectList(null);
        // Dict to DictEdVo
        List<DictEeVo> dictList = new ArrayList<>();
        for(Dict dict : dicts) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);
            dictList.add(dictEeVo);
        }
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(dictList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer cnt = baseMapper.selectCount(wrapper);
        return cnt >  0;
    }


}
