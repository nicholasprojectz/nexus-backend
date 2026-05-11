# Sistema de Gestão Financeira e Patrimonial - Nexus

Este repositório contém o código-fonte da API Backend do trabalho da disciplina de Projeto Temático 1. O objetivo do projeto é aplicar os conceitos de Engenharia de Software e desenvolvimento de APIs RESTful para criar um sistema de gerenciamento de finanças pessoais e investimentos.

📜 Sobre o Projeto
O sistema "Nexus" é uma aplicação que visa gerenciar o controle financeiro de ponta a ponta. Ele permite o registro de receitas e despesas, a criação de categorias personalizadas, o acompanhamento do progresso de metas financeiras e serve como fundação para a gestão de investimentos em Renda Fixa e Variável.

Informações da Disciplina
Universidade: Universidade de Caxias do Sul (UCS)
Disciplina: Projeto Temático 1
Estudante: Nicholas Rodrigues
Ano/Semestre: 2026/1

✨ Funcionalidades

O sistema foi modelado para atender às seguintes operações e regras de negócio:

Cadastros Gerais (CRUD)
Para as entidades principais, foram implementadas as rotas de Inclusão, Alteração, Exclusão e Consulta:
* Usuários: Cadastro de conta e senhas criptografadas para acesso ao sistema.
* Categorias: Cadastro de classificações de gastos personalizadas pelo usuário.
* Movimentações: Registro de transações financeiras contendo valor, data, descrição e tipo (Receita/Despesa).
* Metas Financeiras: Cadastro de objetivos patrimoniais informando valor alvo, prazos e tipos de investimento associados.

Módulos e Regras de Negócio
* Autenticação e Segurança: Login protegido por token JWT. O sistema garante o isolamento total dos dados, onde um usuário acessa e manipula estritamente os seus próprios registros.
* Filtros Dinâmicos de Dados: Otimização de consultas para geração de gráficos. O sistema filtra movimentações por faixas de data, valores e categorias diretamente no banco de dados (via JPQL), evitando lentidão.
* Blindagem de Sistema: Criação automática da categoria padrão "Meta Financeira" no momento do cadastro do usuário, protegida contra exclusão ou renomeação.

Módulo de Investimentos (Estrutura)
* Agregação de Saldos: O sistema calcula automaticamente o saldo atual de uma meta baseado nas movimentações atreladas a ela.
* Tipos de Ativos: Estrutura de dados preparada para classificar aportes em CDI ou Ações (B3), engatilhada para processamento assíncrono de rentabilidade.

🛠️ Tecnologias Utilizadas

Linguagem: Java 17+
Framework Base: Spring Boot 3.x
Segurança: Spring Security (com JSON Web Tokens)
Persistência e ORM: Spring Data JPA / Hibernate
Banco de Dados: SQLite (garantindo portabilidade acadêmica)
