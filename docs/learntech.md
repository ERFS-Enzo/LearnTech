# Tema do projeto

## Identificação

- Nome do projeto: LearnTech
- Tema: gerenciamento de cursos de uma plataforma de ensino
- Objetivo em uma frase: cadastrar cursos e organizá-los por categoria

## Entidade de classificação

- Nome no singular: CategoriaCurso
- Nome no plural: CategoriasCurso
- Exemplo 1: Programação básica.
- Exemplo 2: Desenvolvimento Web

## Entidade principal

- Nome no singular: Curso
- Nome no plural: Cursos
- Código único: id
- Descrição: nome do curso
- Medida quantitativa: carga horária
- Valor monetário: preço
- Data relevante: data de cadastro
- Status: ativo ou inativo

## Relacionamento

- Uma categoria pode possuir vários cursos.
- Cada curso pertence a uma categoria.

## Três exemplos de registros

1. Curso: C# e .NET para Iniciantes — Categoria: Programação básica — Código: CURSO001 — Carga horária: 40 horas — Preço: R$ 149,90 — Status: ativo
2. Curso: Desenvolvimento de APIs REST com ASP.NET Core — Categoria: Desenvolvimento Web — Código: CURSO002 — Carga horária: 30 horas — Preço: R$ 129,90 — Status: ativo
3. Curso: Banco de Dados com SQL — Categoria: Banco de Dados — Código: CURSO003 — Carga horária: 25 horas — Preço: R$ 99,90 — Status: ativo