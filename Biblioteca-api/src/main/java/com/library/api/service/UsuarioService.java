package com.library.api.service;

import com.library.api.dto.UsuarioRequestDTO;
import com.library.api.dto.UsuarioResponseDTO;
import com.library.api.dto.UsuarioUpdateDTO;
import com.library.api.entity.Aluno;
import com.library.api.entity.Professor;
import com.library.api.entity.Usuario;
import com.library.api.exception.*;
import com.library.api.repository.EmprestimoRepository;
import com.library.api.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.library.api.util.TipoUsuario.ALUNO;
import static com.library.api.util.TipoUsuario.PROFESSOR;

@Service
public class UsuarioService {

    ModelMapper mapper;
    PasswordEncoder passwordEncoder;
    UsuarioRepository usuarioRepository;
    EmprestimoRepository emprestimoRepository;

    public UsuarioService(ModelMapper modelMapper, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, EmprestimoRepository emprestimoRepository) {
        this.mapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Não foi possível encontrar emprestimo de id" + id));
        return mapper.map(usuario, UsuarioResponseDTO.class);
    }

    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(UsuarioRequestDTO  usuarioRequestDTO){
        if (usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())){
            throw new EmailJaCadastradoException("Não foi possível cadastrar usuário");
        }

        if (usuarioRequestDTO.getTipo() == ALUNO) {
            Aluno aluno = mapper.map(usuarioRequestDTO, Aluno.class);
            aluno.setSenha(passwordEncoder.encode(aluno.getSenha()));
            usuarioRepository.save(aluno);
            return mapper.map(aluno, UsuarioResponseDTO.class);

        } else if (usuarioRequestDTO.getTipo() == PROFESSOR){
            Professor professor = mapper.map(usuarioRequestDTO, Professor.class);
            professor.setSenha(passwordEncoder.encode(professor.getSenha()));
            usuarioRepository.save(professor);
            return mapper.map(professor, UsuarioResponseDTO.class);
        }
        throw new DadosInvalidosException("Não foi possível cadastrar usuário.");
    } 

    @Transactional
    public void desativarConta(Long usuarioId){
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário de id " + usuarioId + " não encontrado."));
        if (!emprestimoRepository.findEmprestimosAtivos(usuarioId).isEmpty()){
            throw new DesativacaoNaoPermitidaException("Não foi possível desativar conta. Usuário possui emprestimos ativos.");
        }
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizarDados(Long id, UsuarioUpdateDTO  usuarioUpdateDTO){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Não foi possível encontrar usuário de id " + id + "."));

        if (usuarioUpdateDTO.getNome() != null && !usuarioUpdateDTO.getNome().isBlank()){
            usuario.setNome(usuarioUpdateDTO.getNome());
        }
        if (usuarioUpdateDTO.getSenha() != null && !usuarioUpdateDTO.getSenha().isBlank()){
            usuario.setSenha(passwordEncoder.encode(usuarioUpdateDTO.getSenha()));
        }
        if (usuarioUpdateDTO.getEmail() != null && !usuarioUpdateDTO.getEmail().isBlank()){
            usuario.setEmail(usuarioUpdateDTO.getEmail());
        }
        return mapper.map(usuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO buscarPorEmail(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Email não encontrado."));
        return mapper.map(usuario, UsuarioResponseDTO.class);
    }
}
