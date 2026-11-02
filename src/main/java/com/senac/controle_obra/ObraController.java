package com.senac.controle_obra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class ObraController {

    @Autowired
    private FaseObraRepository repository;

    @GetMapping
    public String listarFases(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String usuario = userDetails.getUsername();
        List<FaseObra> fases = repository.findByUsuario(usuario);

        int totalAtividades = fases.stream().mapToInt(FaseObra::getAtividadesTotais).sum();
        int totalExecutadas = fases.stream().mapToInt(FaseObra::getAtividadesExecutadas).sum();
        int totalRefazer = fases.stream().mapToInt(FaseObra::getAtividadesRefazer).sum();

        double progressoGeral = totalAtividades == 0 ? 0 : ((double) totalExecutadas / totalAtividades) * 100;

        model.addAttribute("fases", fases);
        model.addAttribute("totalAtividades", totalAtividades);
        model.addAttribute("totalExecutadas", totalExecutadas);
        model.addAttribute("totalRefazer", totalRefazer);
        model.addAttribute("progressoGeral", Math.round(progressoGeral * 10.0) / 10.0);

        return "index";
    }

    @PostMapping("/adicionar")
    public String adicionarFase(@RequestParam String nome, @RequestParam int totais, @AuthenticationPrincipal UserDetails userDetails) {
        FaseObra fase = new FaseObra(nome, totais, 0, userDetails.getUsername());
        repository.save(fase);
        return "redirect:/";
    }

    @PostMapping("/atualizar")
    public String atualizarProgresso(@RequestParam Long id, @RequestParam int executadas) {
        FaseObra fase = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id inválido"));
        fase.setAtividadesExecutadas(executadas);
        repository.save(fase);
        return "redirect:/";
    }

    @PostMapping("/editar-valores")
    public String editarValores(@RequestParam Long id, @RequestParam int totais) {
        FaseObra fase = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id inválido"));
        fase.setAtividadesTotais(totais);
        repository.save(fase);
        return "redirect:/";
    }

    @PostMapping("/editar-nome")
    public String editarNome(@RequestParam Long id, @RequestParam String novoNome) {
        FaseObra fase = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id inválido"));
        fase.setNome(novoNome);
        repository.save(fase);
        return "redirect:/";
    }

    @PostMapping("/deletar")
    public String deletarFase(@RequestParam Long id) {
        repository.deleteById(id);
        return "redirect:/";
    }
}