package com.noukta.wallpaper.data

import androidx.compose.ui.graphics.Color

enum class Category(val lightColor: Color, val darkColor: Color) {
    //Abstract
    Aesthetic(Color(0xFFE1BEE7), Color(0xFF4A148C)),
    Animal(Color(0xFFFFF9C4), Color(0xFFF57F17)),
    Anime(Color(0xFFB3E5FC), Color(0xFF01579B)),
    //Art
    //Beach
    //Black
    Car(Color(0xFFD7CCC8), Color(0xFF3E2723)),
    Cartoon(Color(0xFFB2EBF2), Color(0xFF006064)),
    Cute(Color(0xFFFFE0B2), Color(0xFFE65100)),
    Fantasy(Color(0xFFB2DFDB), Color(0xFF004D40)),
    Flower(Color(0xFFF8BBD0), Color(0xFF880E4F)),
    Gaming(Color(0xFFD1C4E9), Color(0xFF311B92)),
    //Girly
    //Graffiti
    IPhone(Color(0xFFCFD8DC), Color(0xFF263238)),
    //Love
    //Minimalist
    Movie(Color(0xFFF5F5F5), Color(0xFF212121)),
    Nature(Color(0xFFC8E6C9), Color(0xFF1B5E20)),
    Space(Color(0xFFC5CAE9), Color(0xFF1A237E)),
    SuperHero(Color(0xFFFFCDD2), Color(0xFFB71C1C))
}