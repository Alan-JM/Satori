package com.example.satori_by_aristo.Bitacoras.Operador;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CarruselAdapter extends FragmentStateAdapter {

    public CarruselAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ImagenFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3; // tres imágenes
    }
}
