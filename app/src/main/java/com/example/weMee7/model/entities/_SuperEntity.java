package com.example.weMee7.model.entities;

import java.util.Objects;

/**
 * Superclase de la que heredan
 * todas las clases que representen
 * documentos de alguna coleccion de Firebase,
 * y tiene como unico atributo comun el id (String).
 * Sobreescribe la funcion equals
 */
public abstract class _SuperEntity {
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    /**
     * Dos entidades son iguales
     * si pertenecen a la misma clase (subclase)
     * Y tienen el mismo id
     * @param o
     * @return true / false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        _SuperEntity that = (_SuperEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
