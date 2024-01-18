package io.yao.harp.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.yao.harp.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    String getNameByCodeAndValue(String dictCode, String value);

    List<Dict> findChildrenData(Long id);

    void exportDictData(HttpServletResponse response);

    void importDictData(MultipartFile file);

    List<Dict> findDictByDictCode(String dictCode);
}
