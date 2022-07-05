package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.entities.Usuario;

public interface IUsuarioService {
    public Usuario findByUsername(String username);
    public Usuario findByEmail(String email);
}
