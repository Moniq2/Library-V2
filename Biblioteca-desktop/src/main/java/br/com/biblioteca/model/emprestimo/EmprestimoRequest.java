package br.com.biblioteca.model.emprestimo;

public class EmprestimoRequest {
    private long usuarioId;
    private long livroId;

    public EmprestimoRequest() {}

    public Long getUsuarioId() {
        return usuarioId;
    }
    public Long getLivroId() {
        return livroId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    public void setLivroId(Long livroId) {
        this.livroId = livroId;
    }

    public EmprestimoRequest(Long usuarioId, Long livroId) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
    }
}
