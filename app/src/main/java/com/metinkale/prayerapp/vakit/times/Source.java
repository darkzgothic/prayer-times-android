/*
 * Copyright (c) 2013-2017 Metin Kale
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

package com.metinkale.prayerapp.vakit.times;

import com.metinkale.prayer.R;
import com.metinkale.prayerapp.App;
import com.metinkale.prayerapp.settings.Prefs;
import com.metinkale.prayerapp.utils.Utils;
import com.metinkale.prayerapp.vakit.times.sources.CSVTimes;
import com.metinkale.prayerapp.vakit.times.sources.CalcTimes;
import com.metinkale.prayerapp.vakit.times.sources.DiyanetTimes;
import com.metinkale.prayerapp.vakit.times.sources.FaziletTimes;
import com.metinkale.prayerapp.vakit.times.sources.IGMGTimes;
import com.metinkale.prayerapp.vakit.times.sources.MalaysiaTimes;
import com.metinkale.prayerapp.vakit.times.sources.MoroccoTimes;
import com.metinkale.prayerapp.vakit.times.sources.NVCTimes;
import com.metinkale.prayerapp.vakit.times.sources.SemerkandTimes;

import java.util.Locale;

/**
 * Created by metin on 03.04.2016.
 */
public enum Source {
    Calc(R.string.calculated, 0, CalcTimes.class),
    Diyanet("Diyanet.gov.tr", R.drawable.ic_ditib, DiyanetTimes.class),
    Fazilet("FaziletTakvimi.com", R.drawable.ic_fazilet, FaziletTimes.class),
    IGMG("IGMG.org", R.drawable.ic_igmg, IGMGTimes.class),
    Semerkand("SemerkandTakvimi.com", R.drawable.ic_semerkand, SemerkandTimes.class),
    NVC("NamazVakti.com", R.drawable.ic_namazvakticom, NVCTimes.class),
    Morocco("habous.gov.ma", R.drawable.ic_morocco, MoroccoTimes.class),
    Malaysia("e-solat.gov.my", R.drawable.ic_malaysia, MalaysiaTimes.class),
    CSV("CSV", 0, CSVTimes.class);

    public final Class<? extends Times> clz;
    public final int resId;
    public final String text;

    Source(String text, int resId, Class<? extends Times> clz) {
        this.text = text;
        this.resId = resId;
        this.clz = clz;
    }

    Source(int resTxt, int resIcon, Class<? extends Times> clz) {
        Locale.setDefault(Utils.getLocale());
        this.text = App.get().getString(resTxt);
        this.resId = resIcon;
        this.clz = clz;
    }
}