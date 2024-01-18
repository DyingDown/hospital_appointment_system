package io.yao.harp.hosp.service;

import io.yao.harp.model.hosp.Department;
import io.yao.harp.model.hosp.Schedule;
import io.yao.harp.vo.hosp.DepartmentQueryVo;
import io.yao.harp.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> parameterMap);

    // search
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode, String depcode);

    // get department list tree by hospital code
    List<DepartmentVo> getDeptTree(String hoscode);

    String getDepNameByHoscodeAndDepcode(String hoscode, String depcode);

    Department getDepartment(String hoscode, String depcode);
}
