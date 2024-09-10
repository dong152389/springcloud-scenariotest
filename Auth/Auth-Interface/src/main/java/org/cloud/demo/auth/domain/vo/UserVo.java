package org.cloud.demo.auth.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = -262323364556546343L;
    private Long userId;
    private String userName;
    private String nickName;
    private String email;
    private String sex;
    private String phonenumber;
    private String avatar;
    private String status;
    private String loginIp;
    private Date loginDate;
}
