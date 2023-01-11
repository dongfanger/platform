package com.example.platform.common.api;

/**
 * 枚举了一些常用API提示消息
 * Created by macro on 2019/4/19.
 */
public enum Message {
    Success("操作成功"),
    Failed("操作失败"),
    ValidateFailed("参数检验失败"),
    Unauthorized("暂未登录或token已经过期"),
    Forbidden("没有相关权限"),
    ErrInvalidPassword("用户名密码不匹配"),
    ErrUserNotFound("用户不存在"),
    ErrInvalidOldPassword("初始密码错误"),
    ErrInvalidUserID("无效的用户ID"),
    FileFormatError("文件格式不对，请上传xmind文件"),
    FileImportError("导入失败，请稍后再试"),
    FileExportError("导出失败，请稍后再试");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
