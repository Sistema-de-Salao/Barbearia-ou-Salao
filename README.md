# BarberSystem - Sistema de Agendamento Desktop

Este é um sistema de agendamento para barbearias ou salões de beleza, desenvolvido em **Java Swing** seguindo o padrão **MVC** (Model-View-Controller) com persistência de dados em ficheiros `.txt`.

## Estrutura do Projeto

- `com.barber.model`: Entidades do sistema (Cliente, Profissional, Serviço, Agendamento).
- `com.barber.dao`: Camada de acesso a dados (Persistência em arquivos).
- `com.barber.controller`: Lógica de negócio e validações.
- `com.barber.view`: Interfaces gráficas Swing.
- `data/`: Pasta onde os arquivos `.txt` de dados são armazenados automaticamente.

## Como rodar no IntelliJ IDEA

1. Abra o IntelliJ IDEA.
2. Vá em **File > Open** e selecione a pasta `BarberSystem`.
3. Certifique-se de que o **JDK 11 ou superior** está configurado em **File > Project Structure > Project**.
4. Localize o arquivo `src/Main.java`.
5. Clique com o botão direito no arquivo e selecione **Run 'Main.main()'**.

## Funcionalidades Implementadas

- **Gestão de Cadastros**: Clientes, Profissionais e Serviços.
- **Agendamento Inteligente**: Bloqueio automático de conflitos de horário para o mesmo profissional.
- **Agenda Principal**: Visualização diária com navegação entre datas.
- **Controle de Status**: Marcar atendimentos como concluídos ou cancelados.
- **Relatórios**: Faturação total e quantidade de atendimentos por período.
- **Persistência**: Todos os dados são salvos em arquivos na pasta `data/`.

## Requisitos do Sistema
- Java Development Kit (JDK) 11+
- IntelliJ IDEA (ou qualquer IDE Java)
