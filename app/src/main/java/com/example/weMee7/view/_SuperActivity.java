package com.example.weMee7.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weMee7.activities.AboutFragment;
import com.example.weMee7.activities.AddFragment;
import com.example.weMee7.comun.Avatar;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view.usuario.LoginFragment;
import com.example.weMee7.view.usuario.SettingsFragment;
import com.example.weMee7.view.usuario.HomeFragment;
import com.example.weMee7.view.usuario.PerfilFragment;
import com.example.wemee7.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

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

        //Listener de navegacion hacia atras
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    Fragment fragmentActual = getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);
                    if(fragmentActual != null)
                        personalizarMenu(fragmentActual.getClass().getSimpleName());
                });
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
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(activar && fUser != null)
            setDrawerData(fUser);
    }

    /**
     * Carga datos del usuario
     * en el encabezado del menu hamburguesa
     * @param fUser
     */
    private void setDrawerData(FirebaseUser fUser){
        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ImageView foto = header.findViewById(R.id.ivHeaderFoto);
        TextView nombre = header.findViewById(R.id.tvHeaderNombre);
        TextView cuenta = header.findViewById(R.id.tvHeaderCuenta);
        new UsuarioDAO().obtenerRegistroPorId(fUser.getUid(), resultado -> {
            Usuario usuario = (Usuario)resultado;
            Avatar avatar = new Avatar(this,usuario.getFoto());
            foto.setImageBitmap(avatar.toBitmap());
            foto.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(avatar.getColor())));
            nombre.setText(usuario.getNombre());
            cuenta.setText(fUser.getEmail());
        });

    }

    /**
     * Coloca un nuevo Fragment en el container del layout;
     * si es el HomeFragment, se vacía la pila (no se puede volver atrás) ???
     * @param fragment
     */
    public void colocarFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Si se llama al fragment Home, se vacia la pila
        if(fragmentManager.getBackStackEntryCount() > 0 && fragment instanceof HomeFragment)
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //Reemplazar fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        personalizarMenu(fragment.getClass().getSimpleName());
    }

    /**
     * Se personaliza el titulo de la Toolbar
     * segun el fragmento cargado;
     * si ademas es una de las opciones del menu hamburguesa,
     * esta aparece seleccionada
     * @param nombreFragment
     */
    public void personalizarMenu(String nombreFragment){
        int op = -1;
        int tituloToolbar = R.string.app_name;
        switch(nombreFragment){
            case "HomeFragment":
                op = R.id.nav_Home;
                break;
            case "PerfilFragment":
                op = R.id.nav_perfil;
                tituloToolbar = R.string.menu_perfil;
                break;
            case "AddFragment":
                op = R.id.nav_Add;
                tituloToolbar = R.string.menu_nueva_reunion;
                break;
            case "AboutFragment":
                op = R.id.nav_About;
                tituloToolbar = R.string.menu_about;
                break;
            case "SettingsFragment":
                op = R.id.nav_Settings;
                tituloToolbar = R.string.menu_settings;
                break;
            case "AuthPhoneFragment":
                tituloToolbar = R.string.tag_vincular_tlf;
                break;
            case "PhoneCodeFragment":
                tituloToolbar = R.string.hint_codigo_verificacion;
                break;
            //Para los demás fragments, incluir titulo de barra a partir de aqui !!!
        }
        if(op != -1)
            navigationView.setCheckedItem(op);
        toolbar.setTitle(tituloToolbar);
    }

    /**
     * Selector de opciones del menu hamburguesa
     * @param item The selected item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.nav_Home)
            selectedFragment = new HomeFragment();
        else if (item.getItemId() == R.id.nav_Add)
            selectedFragment = new AddFragment();
        else if (item.getItemId() == R.id.nav_About)
            selectedFragment = new AboutFragment();
        else if (item.getItemId() == R.id.nav_Settings)
            selectedFragment = new SettingsFragment();
        else if (item.getItemId() == R.id.nav_perfil)
            selectedFragment = new PerfilFragment();

        if(selectedFragment != null)
            if(selectedFragment.getClass() == Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container)).getClass())
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                colocarFragment(selectedFragment);

        return true;
    }

    /**
     * Navegacion mediante el boton atras:
     * 1. Si el menu hamburguesa esta abierto, lo cierra.
     * 2. Si esta el fragment Login o Home, se cierra la aplicacion.
     * 3. Si esta otro fragment, vuelve al fragment anterior.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(fragment instanceof LoginFragment || fragment instanceof HomeFragment)
                this.finishAffinity();
            else
                if(getSupportFragmentManager().getBackStackEntryCount() == 0)
                    super.onBackPressed();
                else
                    getSupportFragmentManager().popBackStack();
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

    *** VER activity_base.xml y usages de estas funciones */

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
