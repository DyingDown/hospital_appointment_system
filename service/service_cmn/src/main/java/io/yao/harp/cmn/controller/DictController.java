package io.yao.harp.cmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.yao.harp.cmn.service.DictService;
import io.yao.harp.common.result.Result;
import io.yao.harp.model.cmn.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Schema(description = "data dict api")
@RestController

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
    public void importDictData(MultipartFile file) {
        dictService.importDictData(file);
    }

    @Schema(description = "Get name by dict code and value")
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value) {
        String name = dictService.getNameByCodeAndValue(dictCode, value);
        return name;
    }

    @Schema(description = "Get name by value")
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value) {
        return dictService.getNameByCodeAndValue("", value);
    }

    @Operation(summary = "Get Province by dict code")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode) {
        List<Dict> dictList = dictService.findDictByDictCode(dictCode);
        return Result.ok(dictList);
    }
}
