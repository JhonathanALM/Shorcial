package com.odc.beachodc.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.odc.beachodc.Home;
import com.odc.beachodc.Logout;
import com.odc.beachodc.R;
import com.odc.beachodc.db.BBDD;
import com.odc.beachodc.db.models.Playa;
import com.odc.beachodc.fragments.edit.InfoPlayaFragment;
import com.odc.beachodc.fragments.edit.MapPlayaFragment;
import com.odc.beachodc.fragments.edit.OnlyExtrasPlayaFragment;
import com.odc.beachodc.utilities.Utilities;
import com.odc.beachodc.utilities.ValidacionPlaya;
import com.odc.beachodc.webservices.Request;

import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class EdicionPlaya extends LocationActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //opening transition animations
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

        setContentView(R.layout.activity_home);

        boolean isNew = false;
        try {
            isNew = getIntent().getExtras().getBoolean("nuevo");
            if (isNew)
                ValidacionPlaya.playa = new Playa(true);
        } catch (Exception e){}

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        Utilities.setActionBarCustomize(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        boolean isNew = false;
        try {
            isNew = getIntent().getExtras().getBoolean("nuevo");
        } catch (Exception e){}


        if (isNew)
            inflater.inflate(R.menu.editar, menu);
        else
            inflater.inflate(R.menu.editar_informar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Si se ha clickado en la opcion de cerrar sesión del menu, mostraremos el fragment que nos permitirá cerrar la sesión.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_logout:
                if (Utilities.haveInternet(this)) {
                    Intent intent = new Intent(this, Logout.class);
                    startActivity(intent);
                } else {
                    Crouton.makeText(this, getString(R.string.no_internet), Style.ALERT).show();
                }
                return true;

            case R.id.menu_editar:
                if (Utilities.haveInternet(this)) {
                    if (ValidacionPlaya.validarInfoPlaya(this)){
                        boolean isNew = false;
                        try {
                            isNew = getIntent().getExtras().getBoolean("nuevo");
                        } catch (Exception e){}

                        if (isNew)
                            BBDD.guardarPlaya(this, ValidacionPlaya.playa, true);
                        else
                            BBDD.guardarPlaya(this, ValidacionPlaya.playa, false);
                    }
                } else {
                    Crouton.makeText(this, getString(R.string.no_internet), Style.ALERT).show();
                }

                return true;
            case R.id.menu_informar:
                if (Utilities.haveInternet(this)) {
                    ProgressDialog pd = ProgressDialog.show(this, getResources().getText(R.string.esperar), getResources().getText(R.string.esperar));
                    pd.setIndeterminate(false);
                    pd.setCancelable(false);
                    Request.peticionBorrarPlaya(this, ValidacionPlaya.playa, pd);
                } else {
                    Crouton.makeText(this, getString(R.string.no_internet), Style.ALERT).show();
                }
                return true;
            case android.R.id.home:
                Intent intentH = new Intent(this, Home.class);
                // Para eliminar el historial de activities visitadas ya que volvemos al HOME y asi el boton ATRAS no tenga ningun comportamiento, se resetee.
                intentH.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentH);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            boolean isNew = false;
            try {
                isNew = getIntent().getExtras().getBoolean("nuevo");
            } catch (Exception e){}

            switch (position) {
                case 0:
                    if (isNew)
                        return new InfoPlayaFragment();
                    else
                        return new OnlyExtrasPlayaFragment();
                case 1:
                    return new MapPlayaFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            boolean isNew = false;
            try {
                isNew = getIntent().getExtras().getBoolean("nuevo");
            } catch (Exception e){}

            if (isNew)
                return 2;
            else
                return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            boolean isNew = false;
            try {
                isNew = getIntent().getExtras().getBoolean("nuevo");
            } catch (Exception e){}
            switch (position) {
                case 0:
                    if (isNew)
                        return getString(R.string.title_section_edit_infoplaya).toUpperCase(l);
                    else
                        return ValidacionPlaya.playa.nombre.toUpperCase(l);
                case 1:
                    return getString(R.string.title_section_edit_mapplaya).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //closing transition animations
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
        System.gc();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        Runtime.getRuntime().gc();
    }

}
