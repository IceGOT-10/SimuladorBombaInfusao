# Simulador de Bomba de Infusão - Versão Beta

Este projeto é um simulador de Interface Homem-Máquina (IHM) para treinamento médico, baseado no modelo de bombas de infusão Lifemed. Ele foi desenvolvido para proporcionar uma experiência realista de operação de hardware médico em um ambiente Android.

## 🚀 Funcionalidades Implementadas

- **Interface Landscape Imersiva**: Layout horizontal otimizado para simular a tela de uma bomba real, com modo tela cheia automático.
- **Controle de Vazão e VAI**: Ajuste de parâmetros (Vazão e Volume a Injetar) diretamente via teclado numérico na tela principal.
- **Persistência com Room**: Banco de dados local para armazenamento e consulta de medicamentos.
- **Menu de Configurações**: Tela dedicada para seleção de drogas e ajustes do sistema.
- **Função BOLUS**: Simulação de infusão rápida mantendo o botão pressionado.
- **Sistema de Alertas**: Alarme sonoro e visual (tela vermelha) ao concluir a infusão.
- **Relógio em Tempo Real**: Barra de status superior com hora atualizada.
- **Animação de Fluxo**: Indicadores visuais dinâmicos durante a operação.

## 🛠️ Tecnologias Utilizadas

- **Arquitetura**: MVVM (ViewModel, LiveData)
- **Persistência**: Room Database
- **Linguagem**: Java
- **UI**: XML Customizado (Estilo Lifemed)

## 📦 Instalação e Testes

1. Clone o repositório.
2. Abra o projeto no Android Studio.
3. Sincronize o Gradle (versão 9.3.1 / AGP 8.3.2).
4. Execute o aplicativo em um emulador ou dispositivo físico em modo Paisagem.

---
**Observação**: Esta é uma **Versão Beta (v0.5.0)** destinada apenas para fins de testes e validação de interface. Não deve ser utilizada para procedimentos médicos reais.
