package com.fridayapp.todo;

import java.io.Serializable;

public interface PermissionListener extends Serializable {
    void permissionResult(boolean hasPermission);
}