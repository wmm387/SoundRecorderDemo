package wangyuanwmm.soundrecorderdemo.Util;

/**
 *监听数据库中的添加/重命名项
 * Created by Administrator on 2017/5/2.
 */

public interface OnDatabaseChangedListener {
    void onNewDatabaseEntryAdded();
    void onDatabaseEntryRenamed();
}
