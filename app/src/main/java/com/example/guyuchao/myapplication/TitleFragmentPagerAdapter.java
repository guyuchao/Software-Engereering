package com.example.guyuchao.myapplication;
    /*
     * Copyright (C) 2012 www.amsoft.cn
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
 import android.support.v4.app.Fragment;
 import android.support.v4.app.FragmentManager;
 import android.support.v4.app.FragmentPagerAdapter;

 import java.util.ArrayList;
 import java.util.List;

/**
 * © 2012 amsoft.cn 名称：TitleFragmentPagerAdapter.java 描述：一个通用的Fragment适配器
 *
 * @author wolf
 * @version v1.0
 * @date：2016-3-7 上午10:57:53
 */
public class TitleFragmentPagerAdapter extends FragmentPagerAdapter {

    /**
     * The m fragment list.
     */
    private List<Fragment> mFragmentList = null;

    private String[] titles;

    /**
     * Instantiates a new ab fragment pager adapter.
     *
     * @param mFragmentManager the m fragment manager
     * @param fragmentList     the fragment list
     */
    public TitleFragmentPagerAdapter(FragmentManager mFragmentManager,
                                     ArrayList<Fragment> fragmentList) {
        super(mFragmentManager);
        mFragmentList = fragmentList;
    }

    /**
     * titles是给TabLayout设置title用的
     *
     * @param mFragmentManager
     * @param fragmentList
     * @param titles
     */
    public TitleFragmentPagerAdapter(FragmentManager mFragmentManager,
                                     List<Fragment> fragmentList, String[] titles) {
        super(mFragmentManager);
        mFragmentList = fragmentList;
        this.titles = titles;
    }

    /**
     * 描述：获取数量.
     *
     * @return the count
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * 描述：获取索引位置的Fragment.
     *
     * @param position the position
     * @return the item
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position < mFragmentList.size()) {
            fragment = mFragmentList.get(position);
        } else {
            fragment = mFragmentList.get(0);
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && titles.length > 0)
            return titles[position];
        return null;
    }
}