package com.senac.controle_obra;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/")
public class ObraController {

    @Autowired
    private FaseObraRepository repository;

    @GetMapping
    public String listarFases(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String usuario = userDetails.getUsername();

        // FILTRO CRUCIAL: Busca estritamente os dados do usuário autenticado
        List<FaseObra> fases = repository.findByUsuario(usuario);

        int totalAtividades = fases.stream().mapToInt(FaseObra::getAtividadesTotais).sum();
        int totalExecutadas = fases.stream().mapToInt(FaseObra::getAtividadesExecutadas).sum();
        int totalRefazer = fases.stream().mapToInt(FaseObra::getAtividadesRefazer).sum();

        double progressoGeral = totalAtividades == 0 ? 0 : ((double) totalExecutadas / totalAtividades) * 100;
        String nomeDaObraAtual = fases.isEmpty() ? "Minha Obra Sem Nome" : fases.get(0).getNomeObra();

        model.addAttribute("fases", fases);
        model.addAttribute("nomeObra", nomeDaObraAtual);
        model.addAttribute("totalAtividades", totalAtividades);
        model.addAttribute("totalExecutadas", totalExecutadas);
        model.addAttribute("totalRefazer", totalRefazer);
        model.addAttribute("progressoGeral", Math.round(progressoGeral * 10.0) / 10.0);

        return "index";
    }

    @PostMapping("/adicionar")
    public String adicionarFase(@RequestParam String nome, @RequestParam int totais,
                                @RequestParam String nomeObra, @AuthenticationPrincipal UserDetails userDetails) {
        // Grava explicitamente o nome do usuário logado na nova linha do banco
        FaseObra fase = new FaseObra(nome, totais, 0, userDetails.getUsername(), nomeObra);
        repository.save(fase);
        return "redirect:/";
    }

    @PostMapping("/atualizar")
    public String atualizarProgresso(@RequestParam Long id, @RequestParam int executadas, @AuthenticationPrincipal UserDetails userDetails) {
        FaseObra fase = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id inválido"));

        // VALIDAÇÃO DE SEGURANÇA SEGUNDA CAMADA: Impede alteração cruzada
        if (fase.getUsuario().equals(userDetails.getUsername())) {
            fase.setAtividadesExecutadas(executadas);
            repository.save(fase);
        }
        return "redirect:/";
    }

    @PostMapping("/editar-valores")
    public String editarValores(@RequestParam Long id, @RequestParam int totais, @AuthenticationPrincipal UserDetails userDetails) {
        FaseObra fase = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id inválido"));

        if (fase.getUsuario().equals(userDetails.getUsername())) {
            fase.setAtividadesTotais(totais);
            repository.save(fase);
        }
        return "redirect:/";
    }

    @PostMapping("/editar-nome")
    public String editarNome(@RequestParam Long id, @RequestParam String novoNome, @AuthenticationPrincipal UserDetails userDetails) {
        FaseObra fase = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id inválido"));

        if (fase.getUsuario().equals(userDetails.getUsername())) {
            fase.setNome(novoNome);
            repository.save(fase);
        }
        return "redirect:/";
    }

    @PostMapping("/deletar")
    public String deletarFase(@RequestParam Long id, @AuthenticationPrincipal UserDetails userDetails) {
        FaseObra fase = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id inválido"));

        if (fase.getUsuario().equals(userDetails.getUsername())) {
            repository.deleteById(id);
        }
        return "redirect:/";
    }

    @GetMapping("/pdf")
    public void gerarPdf(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_obra.pdf");

        String usuario = userDetails.getUsername();
        // PDF também usa o filtro isolado por usuário
        List<FaseObra> fases = repository.findByUsuario(usuario);

        int totalAtividades = fases.stream().mapToInt(FaseObra::getAtividadesTotais).sum();
        int totalExecutadas = fases.stream().mapToInt(FaseObra::getAtividadesExecutadas).sum();
        double progressoGeral = totalAtividades == 0 ? 0 : ((double) totalExecutadas / totalAtividades) * 100;
        String nomeDaObraAtual = fases.isEmpty() ? "Minha Obra" : fases.get(0).getNomeObra();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font fontHeaderTabela = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font fontCorpoTabela = FontFactory.getFont(FontFactory.HELVETICA, 10);

        Paragraph titulo = new Paragraph("Relatorio de Evolucao da Obra", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        Paragraph sub = new Paragraph("Empreendimento: " + nomeDaObraAtual + "\nResponsavel: " + usuario + "\n\n", fontSubtitulo);
        document.add(sub);

        Paragraph resumo = new Paragraph("Rendimento Geral: " + Math.round(progressoGeral * 10.0) / 10.0 + "%\n" +
                "Total de Tarefas: " + totalAtividades + "\n" +
                "Executadas: " + totalExecutadas + "\n" +
                "Restantes: " + (totalAtividades - totalExecutadas) + "\n\n", fontSubtitulo);
        document.add(resumo);

        PdfPTable tabela = new PdfPTable(5);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[] {3f, 1f, 1.2f, 1.2f, 1.3f});

        String[] headers = {"Nome da Etapa", "Total", "Executadas", "Restantes", "Progresso"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontHeaderTabela));
            cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabela.addCell(cell);
        }

        for (FaseObra fase : fases) {
            tabela.addCell(new PdfPCell(new Phrase(fase.getNome(), fontCorpoTabela)));
            tabela.addCell(new PdfPCell(new Phrase(String.valueOf(fase.getAtividadesTotais()), fontCorpoTabela)));
            tabela.addCell(new PdfPCell(new Phrase(String.valueOf(fase.getAtividadesExecutadas()), fontCorpoTabela)));
            tabela.addCell(new PdfPCell(new Phrase(String.valueOf(fase.getRestante()), fontCorpoTabela)));
            tabela.addCell(new PdfPCell(new Phrase(Math.round(fase.getPorcentagem() * 10.0) / 10.0 + "%", fontCorpoTabela)));
        }

        document.add(tabela);
        document.close();
    }
}