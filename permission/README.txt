//6.0以上权限校验工具类，使用方法如下：
PermissionUtils.newInstance(context).
    checkPermissons(OnPermissionGrantedListener listener,String... permissions);
权限授权成功会回调listener.onPermissionGranted();

