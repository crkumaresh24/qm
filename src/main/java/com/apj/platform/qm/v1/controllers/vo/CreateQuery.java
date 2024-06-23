package com.apj.platform.qm.v1.controllers.vo;

import com.apj.platform.qm.v1.constants.QmErrorCodes;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateQuery {
    @NotBlank(message = QmErrorCodes.ERR_QUERY_NAME_BLANK)
    private String name;
    @NotBlank(message = QmErrorCodes.ERR_QUERY_TXT_BLANK)
    private String query;
}
