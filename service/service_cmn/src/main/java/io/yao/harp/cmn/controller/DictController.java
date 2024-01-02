package io.yao.harp.cmn.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.yao.harp.cmn.service.DictService;
import io.yao.harp.common.result.Result;
import io.yao.harp.model.cmn.Dict;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Schema(description = "data dict api")
@RestController
@CrossOrigin
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @Schema(description = "Get children data by id")
    @GetMapping("findChildrenById/{id}")
    public Result findChildrenById(@PathVariable Long id) {
        List<Dict> childrenData = dictService.findChildrenData(id);
        return Result.ok(childrenData);
    }

    @Schema(description = "Export into excel file")
    @GetMapping("exportDictData")
    public void exportDictData(HttpServletResponse response) {
        dictService.exportDictData(response);
    }

    @Schema(description = "Import data from excel file")
    @PostMapping("importDictData")
    public void importtDictData(MultipartFile file) {
        dictService.importDictData(file);
    }

}
