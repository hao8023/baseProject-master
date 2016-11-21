package com.phoenix.base.fragment;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.phoenix.base.AppContext;
import com.phoenix.base.BaseApplication;
import com.phoenix.base.R;
import com.phoenix.base.utils.DataCleanUtils;
import com.phoenix.base.utils.DialogHelp;
import com.phoenix.base.utils.FileUtil;
import com.phoenix.base.utils.MethodsCompat;
import com.phoenix.base.utils.UIHelper;

import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.utils.FileUtils;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by flashing on 2016/6/9.
 */
public class MineFragment extends Fragment {
    @InjectView(R.id.cache)
    TextView cache;
    @InjectView(R.id.rl_clean_cache)
    LinearLayout rlCleanCache;
    @InjectView(R.id.version)
    TextView version;
    private String cacheSize;
    private String versionName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.inject(this, view);
        initData();
        return view;
    }

    private void initData() {
        cacheSize = DataCleanUtils.getTotalCacheSize(getActivity());
        Logger.d(cacheSize);
        /**获取版本号，设置版本*/
        versionName = getVersionName();
        version.setText(versionName);
        caculateCacheSize();
    }


    /**
     * 计算缓存的大小
     */
    private void caculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtil.getDirSize(filesDir);
        fileSize += FileUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat
                    .getExternalCacheDir(getActivity());
            fileSize += FileUtil.getDirSize(externalCacheDir);
            fileSize += FileUtil.getDirSize(new File(
                    FileUtils.getSDCardPath()
                            + File.separator + HttpConfig.CACHEPATH));
        }
        if (fileSize > 0)
            cacheSize = FileUtil.formatFileSize(fileSize);
        cache.setText(cacheSize);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.rl_clean_cache})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_clean_cache://清理缓存
                onClickCleanCache();
                break;
        }
    }

    /**
     * 清理缓存
     */
    private void onClickCleanCache() {
        DialogHelp.getConfirmDialog(getActivity(), "是否清空缓存?", new DialogInterface.OnClickListener
                () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UIHelper.clearAppCache(getActivity());
                cache.setText("0KB");
            }
        }).show();
    }

    /**
     * 版本号
     */
    public static String getVersionName() {
        String name = "";
        try {
            name = BaseApplication
                    .context()
                    .getPackageManager()
                    .getPackageInfo(BaseApplication.context().getPackageName(),
                            0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            name = "";
        }
        return name;
    }

}
