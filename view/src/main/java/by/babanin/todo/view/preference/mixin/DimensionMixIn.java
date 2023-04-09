package by.babanin.todo.view.preference.mixin;

import java.awt.Dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class DimensionMixIn {

    @JsonIgnore
    Dimension size;
}
