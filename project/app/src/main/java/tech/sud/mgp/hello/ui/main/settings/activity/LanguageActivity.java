package tech.sud.mgp.hello.ui.main.settings.activity;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.Locale;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.event.ChangeLanguageEvent;
import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.ui.main.activity.MainActivity;
import tech.sud.mgp.hello.ui.main.settings.adapter.LangAdapter;
import tech.sud.mgp.hello.ui.main.settings.model.LangCellType;
import tech.sud.mgp.hello.ui.main.settings.model.LangModel;
import tech.sud.mgp.hello.ui.main.utils.HSLanguageUtils;

/**
 * 切换语言
 */
public class LanguageActivity extends BaseActivity {

    private View viewBack;
    private RecyclerView langRecyclerview;
    private LanguageViewModel viewModel = new LanguageViewModel();
    private LangAdapter mAdapter = new LangAdapter(viewModel.languages);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_language_change;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        viewBack = findViewById(R.id.view_back);
        langRecyclerview = findViewById(R.id.lang_recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.currentLanguage();
        viewModel.createLangCellData(getResources().getStringArray(R.array.language_list));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        langRecyclerview.setAdapter(mAdapter);
        langRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            LangModel lang = viewModel.languages.get(position);
            viewModel.clickCell(lang);
            mAdapter.notifyDataSetChanged();
            changeLanguage(lang);
        });
        viewBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void changeLanguage(LangModel item) {
        LiveEventBus.get(LiveEventBusKey.KEY_CHANGE_LANGUAGE).post(new ChangeLanguageEvent());
        viewModel.saveLangCellType(item.cellType);
        boolean isRelaunchApp = false;
        Utils.Consumer<Boolean> consumer = aBoolean -> toMainActivity();
        if (item.cellType == LangCellType.Follow) {
            HSLanguageUtils.applySystemLanguage(isRelaunchApp, consumer);
        } else {
            Locale locale = viewModel.converLocale(item.cellType);
            HSLanguageUtils.applyLanguageReal(locale, isRelaunchApp, consumer);
        }

    }

    /**
     * 切换语言后去首页
     */
    private void toMainActivity() {
        ActivityUtils.finishAllActivitiesExceptNewest();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

}