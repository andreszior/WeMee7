package com.example.weMee7.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.weMee7.activities.AboutFragment;
import com.example.weMee7.activities.AddFragment;
import com.example.weMee7.view.usuario.HomeFragment;
import com.example.weMee7.view.usuario.PerfilFragment;
import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;
import com.google.android.material.navigation.NavigationView;

/**
 * Clase abstracta que implementa
 * metodos comunes a todas las Activities de la app.
 * Debe ser heredada por todas las Activities.
 */
//
public abstract class _SuperActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Establecer layout comun y color de barra
        getWindow().setStatusBarColor(Color.parseColor("#2286A9"));
        setContentView(R.layout.activity_base);

        //Asignar componentes
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Habilita o deshabilita
     * el menu hamburguesa en la barra superior
     * @param activar true : false
     */
    public void setDrawerMenu(boolean activar){
        toggle.setDrawerIndicatorEnabled(activar);
        drawerLayout.setDrawerLockMode(activar ?
                DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void colocarFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        drawerLayout.closeDrawer(GravityCompat.START);

        int op;
        switch(fragment.getClass().getSimpleName()){
            case "HomeFragment":
               op = R.id.nav_Home;
               break;
            case "PerfilFragment":
                op = R.id.nav_perfil;
                break;
            case "AddFragment":
                op = R.id.nav_Add;
                break;
            case "AboutFragment":
                op = R.id.nav_About;
                break;
            default:
                op = -1;
        }
        if(op != -1)
            navigationView.setCheckedItem(op);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.nav_Home)
            selectedFragment = new HomeFragment();
        else if (item.getItemId() == R.id.nav_Add)
            selectedFragment = new AddFragment();
        else if (item.getItemId() == R.id.nav_About)
            selectedFragment = new AboutFragment();
        else if (item.getItemId() == R.id.nav_perfil)
            selectedFragment = new PerfilFragment();
        else if (item.getItemId() == R.id.nav_Logout)
            new ValidarUsuario(this).cerrarSesion();

        if(selectedFragment != null)
            colocarFragment(selectedFragment);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    /**
     * Función para mandar mensajes Toast
     * @param idMensaje
     */
    public void lanzarMensaje(int idMensaje){
        Toast.makeText(this,getString(idMensaje),Toast.LENGTH_SHORT).show();
    }

    /**
     * Comprueba si hay conexión a Internet.
     * Si no la hay, muestra un mensaje y devuelve true
     * @return true / false
     */
    public boolean noHayConexion() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) {//No hay conexion
                Toast.makeText(this,
                        getString(R.string.msj_offline), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;//Hay conexion
    }


    /* FUNCIONES DE ANIMACION DE CARGA.
    PERMITE LLAMAR A UNA FUNCION PARA QUE SE HAGA VISIBLE
    UN LAYOUT CON UNA PROGRESSBAR CIRCULAR,
    Y OTRA PARA OCULTARLA.
    PARA ELLO, ES NECESARIO QUE EN EL LAYOUT DE LA ACTIVIDAD
    EXISTA UN CONSTRAINT LAYOUT CON UNA PROGRESSBAR
    CON @ID capaCargando; Y UN FRAGMENT CONTAINER VIEW
    LLAMADO fragmentContainer.

    *** VER activity_usuario.xml y usages de estas funciones */

    /**
     * Muestra la animación de carga
     * y puede ocultar el contenido el fragment
     * @param mostrar true / false
     */
    public void setCargando(boolean mostrar){
        findViewById(R.id.capaCargando).setVisibility(View.VISIBLE);
        if(mostrar)
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    /**
     * Oculta la animacion de carga
     * y vuelve a mostrar el contenido del fragment
     */
    public void ocultarCargando(){
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        findViewById(R.id.capaCargando).setVisibility(View.GONE);
    }
}
