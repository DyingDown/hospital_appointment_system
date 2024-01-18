package io.yao.harp.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.BeanUtil;
import io.yao.harp.hosp.repository.DepartmentRepository;
import io.yao.harp.hosp.service.DepartmentService;
import io.yao.harp.model.hosp.Department;
import io.yao.harp.model.hosp.Schedule;
import io.yao.harp.vo.hosp.DepartmentQueryVo;
import io.yao.harp.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;


    // upload department information
    @Override
    public void save(Map<String, Object> parameterMap) {
        String jsonString = JSONObject.toJSONString(parameterMap);
        Department department = JSONObject.parseObject(jsonString, Department.class);

        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        if(departmentExist != null) {
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);
        Example<Department> example = Example.of(department, matcher);
        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> getDeptTree(String hoscode) {
        List<DepartmentVo> result = new ArrayList<>();

        // get all department by hoscode
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);

        List<Department> departmentList = departmentRepository.findAll(example);

        // group the department by department code
        Map<String, List<Department>> departmentMap = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        for(Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {
            // department big code
            String deptBigCode = entry.getKey();
            // all departments that belong to the parent department
            List<Department> deptList = entry.getValue();

            // root department
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(deptBigCode);
            departmentVo.setDepname(deptList.get(0).getDepname()); // FIXME: may be deptList is null?

            // children departments
            List<DepartmentVo> children = new ArrayList<>();
            for(Department dept : deptList) {
                DepartmentVo childDeptVo = new DepartmentVo();
                childDeptVo.setDepcode(dept.getDepcode());
                childDeptVo.setDepname(dept.getDepname());
                children.add(childDeptVo);
            }

            departmentVo.setChildren(children);
            result.add(departmentVo);
        }
        return result;
    }

    @Override
    public String getDepNameByHoscodeAndDepcode(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null) {
            return department.getDepname();
        }
        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
