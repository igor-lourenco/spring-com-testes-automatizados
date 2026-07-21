## Fundamentos de testes automatizados

### Tipos de testes
- `Unitário: `
  Teste feito pelo desenvolvedor, responsável por validar o comportamento de unidades funcionais de código. Nesse contexto, entende-se como unidade funcional qualquer porção de código que através de algum estímulo seja capaz de gerar um comportamento esperado (na prática: métodos de uma classe). Um teste unitário não pode acessar outros componentes ou recursos externos (arquivos, bd, rede, web services, etc.).

- `Integração: `
  Teste focado em verificar se a comunicação entre componentes / módulos da aplicação, e também recursos externos, estão interagindo entre si corretamente.

- `Funcional: `
  É um teste do ponto de vista do usuário, se uma determinada funcionalidade está executando corretamente, produzindo o resultado ou comportamento desejado pelo usuário.

#### Beneficios:

-	Detectar facilmente se mudanças violaram as regras
-	É uma forma de documentação (comportamento e entradas/saídas esperadas)
-	Redução de custos em manutenções, especialmente em fases avançadas
-	Melhora design da solução, pois a aplicação testável precisa ser bem delineada

##
###  TDD - Test Driven Development
É um método de desenvolver software. Consiste em um desenvolvimento guiado pelos testes.

#### Princípios / vantagens:
-	Foco nos requisitos
-	Tende a melhorar o design do código, pois o código deverá ser testável
-	Incrementos no projeto têm menos chance de quebrar a aplicação

#### Processo básico:
1.	Escreva o teste como esperado (naturalmente que ele ainda estará falhando)
2.	Implemente o código necessário para que o teste passe
3.	Refatore o código conforme necessidade

##
### Boas práticas e padrões

#### Nomenclatura de um teste:
-	`<AÇÃO>` should `<EFEITO>` [when `<CENÁRIO>`]

#### Padrão AAA
-	`Arrange:` instancie os objetos necessários
-	`Act:` execute as ações necessárias
-	`Assert:` declare o que deveria acontecer (resultado esperado)

#### Princípio da inversão de dependência (SOLID)
-	Se uma classe A depende de uma instância da classe B, não tem como testar a classe A isoladamente. Na verdade nem seria um teste unitário.
-	A inversão de controle ajuda na testabilidade, e garante o isolamento da unidade a ser testada.

#### Independência / isolamento
-	Um teste não pode depender de outros testes, nem da ordem de execução

#### Cenário único
-	O teste deve ter uma lógica simples, linear
-	O teste deve testar apenas um cenário
-	Não use condicionais e loops

#### Previsibilidade
-	O resultado de um teste deve ser sempre o mesmo para os mesmos dados
-	Não faça o resultado depender de coisas que variam, tais como timestamp atual e valores aleatórios.

##
### Annotations usadas nas classes de teste

| Anotação                                  | Descrição |
|:------------------------------------------| :--- |
| `@SpringBootTest`                         | Carrega o contexto da aplicação (teste de integração) |
| `@SpringBootTest` `@AutoConfigureMockMvc` | Carrega o contexto da aplicação (teste de integração & web) Trata as requisições sem subir o servidor |
| `@WebMvcTest(Classe.class)`               | Carrega o contexto, porém somente da camada web (teste de unidade: controlador) |
| `@ExtendWith(SpringExtension.class)`      | Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component) |
| `@DataJpaTest`                            | Carrega somente os componentes relacionados ao Spring Data JPA. Cada teste é transacional e dá rollback ao final. (teste de unidade: repository) |

##
### Fixture

Uma forma de organizar melhor o código dos testes e evitar repetições


| JUnit 5 | JUnit 4 | Objetivo |
| :--- | :--- | :--- |
| `@BeforeAll` | `@BeforeClass` | Preparação antes de todos testes da classe (método estático) |
| `@AfterAll` | `@AfterClass` | Preparação depois de todos testes da classe (método estático) |
| `@BeforeEach` | `@Before` | Preparação antes de cada teste da classe |
| `@AfterEach` | `@After` | Preparação depois de cada teste da classe |


---
# Avançando nos testes unitários

## Abordagens de teste

O teste de software busca descobrir sistematicamente diferentes classes de erros com tempo e esforço mínimos.<br>
Os testes são divididos principalmente em dois grupos: caixa branca e caixa preta.

### Caixa branca
* **Acesso interno:** O testador tem acesso total à estrutura interna e ao código-fonte da aplicação.
* **Foco principal:** Garantir que os componentes internos do software estejam concisos e funcionando corretamente.
* **Análise de caminhos:** Avalia os fluxos e caminhos básicos de execução do código para que sejam devidamente testados.
* **Exemplo clássico:** Os testes unitários são o principal exemplo desta categoria.

### Caixa preta
* **Foco nos requisitos:** Baseia-se nas ações que o software deve desempenhar, focando exclusivamente nos requisitos da aplicação.
* **Código ignorado:** O código-fonte não é considerado; o testador avalia como o sistema funciona externamente, não seus elementos constitutivos.
* **Exemplos clássicos:** Os testes de integração e de API são os principais exemplos desta técnica.

![Imagem1.png](images%2FImagem1.png)

Uma das principais vantagens dos testes unitários por exemplo é proteger os recursos já implementados contra quebras durante alterações no código. Proporciona ao desenvolvedor segurança e proteção da aplicação contra bugs.

---

## Principais anotações Mockito

| Anotação       | Descrição |
|:---------------| :--- |
| `@Mock`        | Cria um objeto falso que simula o comportamento de um componente real. Substitui dependências externas do sistema, sendo muito utilizado para injetar classes como `Repository` ou `Service`. |
| `@Spy`         | Encapsula e "espiona" a instância de um objeto real. Delega as chamadas de métodos para o objeto verdadeiro, rastreando suas execuções e parâmetros. Usado para simular métodos da própria classe sob teste ou em sistemas legados. |
| `@InjectMocks` | Instancia automaticamente o objeto que está sendo testado. Injeta de forma automática todas as dependências que foram criadas usando as anotações `@Mock` e `@Spy`. |

### Diferença entre @Mock e @Spy

* **@Mock (Objeto Falso):** Cria uma instância totalmente vazia (mockada). Se você chamar um método dele sem programar o comportamento antes, ele não executará o código real e retornará o valor padrão (como `null` ou `0`).
* **@Spy (Objeto Real):** Cria uma cópia de uma instância real existente. Se você chamar um método dele sem programar nenhum comportamento, ele executará o código verdadeiro do método e retornará o resultado real.

---
## Cobertura de código com Jacoco

### Introdução à cobertura de código

* No processo de desenvolvimento de software um dos principais objetivos é criar aplicações de alta qualidade e livres de falhas, atendendo aos requisitos funcionais e não funcionais.
* Uma das partes principais deste processo é o teste de software, que tem como objetivo descobrir sistematicamente diferentes classes de erros com uma quantidade de tempo e esforço mínimos. 
* Uma das principais vantagens ao implementar os testes unitários, por exemplo, é proteger os recursos já implementados de serem quebrados à medida que o código muda. Além de proporcionar ao desenvolvedor um senso de proteção da aplicação contra bugs.
* No entanto, alguns autores defendem que somente implementar os testes unitários não é o suficiente. Neste caso, muitos recomendam abordagens de cobertura de código.
* Cobertura de código é uma métrica que indica a porcentagem de código que está coberta por ao menos um teste automatizado.
  * **Exemplo**: Uma cobertura de 90% indica que 10% do código não está coberto por nenhum teste automatizado.
* A cobertura de testes é muito recomendada em alguns contextos desde o princípio do desenvolvimento do software por eliminar possíveis bugs ou permitir que sejam descobertos no estágio inicial do desenvolvimento.
* Podemos dizer que a cobertura de código é uma parte que compõe a cobertura de testes (*test coverage*), que é definido como métrica de teste de software que mede a quantidade de testes executados, dado um conjunto de casos de testes.
* Enquanto a cobertura de código é uma medida quantitativa (número de linhas de código que foram executadas pelos testes), a cobertura de teste é uma medida qualitativa, permitindo validar a implementação dos requisitos do produto. 
* Para realizar a cobertura de código de maneira adequada é necessário ter acesso aos componentes internos (classes e funções) da aplicação.

### Tipos Básicos de Cobertura de Código

#### Statement Coverage (Line coverage)

* Usado para verificar quantas instruções ou comandos do código são executadas;
* Também é chamado de *line coverage* por alguns autores;
* O cálculo do percentual de *statement coverage* pode ser calculado da seguinte forma:
  * **Statement coverage** = Número de statements executados / Número total de statements * 100

##### Exemplo:
![imagem2.png](images%2Fimagem2.png)<br>
*Fonte: [Baeldung CS - Code Coverage](https://www.baeldung.com/cs/code-coverage)*

* Considerando 3 cenários, temos:
  * **Para a = 3, b = 5:** serão executadas as linhas 1, 2, 3 e 8. Desta forma temos 4 linhas de 8, o que significa que temos 4/8 ou 50% de cobertura.
  * **Para a = 3, b = -5:** serão executadas as linhas 1, 2, 4, 5 e 8, ou seja, 5/8 o que equivale a 63% de cobertura.
  * **Para a = 10, b = -10:** serão executadas as linhas 1, 2, 4, 6, 7 e 8, ou seja, 75%.
  * Neste caso, para termos uma cobertura de 100% no método soma, todos os 3 cenários devem ser considerados.
* A vantagem desta abordagem está em permitir verificar diferentes caminhos e quais deles não estão cobertos.

---

#### Branch Coverage

* Verifica se cada ramificação de cada estrutura de controle (incluindo `if/else`, `switch case`, `for`, `while`) é executada;
* O cálculo do percentual de *branch coverage* pode ser calculado da seguinte forma:
  * **Branch coverage** = Número de branchs executadas / Número total de branchs * 100

##### Exemplo:
![imagem3.png](images%2Fimagem3.png)<br>
*Fonte: [Baeldung CS - Code Coverage](https://www.baeldung.com/cs/code-coverage)*

![imagem4.png](images%2Fimagem4.png)

* Considerando os 2 cenários:
  * **Para a = 1:** será executada as linhas 1 e 3, ou seja, 2/3 equivalente a 75%;
  * **Para a = 4:** serão executadas as linhas 1-3, ou seja, 100%.
  * Neste caso, ambos os cenários oferecem uma cobertura de 100%.
* Algumas vantagens desta abordagem:
  * Permite identificar comportamentos não previstos;
  * Permite mapear áreas do código-fonte que outras abordagens não mapeiam.



#### Function Coverage

* A cobertura de função verifica se cada função de um programa está sendo chamada pelo menos uma vez.
* **Exemplo:** No caso de uma aplicação composta por uma única função ou método, a implementação de um único teste de unidade para este método resultará em uma cobertura de 100%.


##### Discussão

* **Qual o percentual de cobertura a ser perseguido?**
* Apesar de a ideia parecer ótima, alcançar os 100% de cobertura de código não deveria ser uma meta absoluta, pois existem trechos que não precisam diretamente de serem testados.
  * *Exemplo:* Códigos que podemos gerar automaticamente com a própria IDE, como Getters e Setters.
* É uma decisão difícil escolher qual trecho de código não precisa ser testado. O fato é que se você precisar priorizar, teste aqueles métodos que são complicados e/ou importantes. Use o número de cobertura para ajudá-lo a identificar trechos que não estão testados.
* Alcançar os 100% de cobertura é desejável, mas não é uma garantia que o seu sistema seja à prova de defeitos.

---

#### Ferramentas para Cobertura

* Algumas das principais ferramentas de cobertura de testes são:
  * **JaCoCo** no contexto do Java;
  * **Istanbul** no contexto do Javascript;
  * **Coverage.py** no contexto do Python;
  * **NCover** no contexto do .NET.
* Vamos focar na utilização do JaCoCo.

### JaCoCo

* JaCoCo é uma ferramenta de código aberto (*open-source*) usada para mensurar a cobertura de código em aplicações;
* A partir de relatórios visuais é possível identificar as partes do código que estão cobertas e que ainda faltam cobertura;
* O JaCoCo implementa 3 métricas principais para cobertura, sendo:
  * Line Coverage / Statement;
  * Branch Coverage;
  * **Cyclomatic complexity:** A partir de uma combinação linear apresenta o número de caminhos que necessitam cobertura.
* O JaCoCo auxilia o usuário na visualização e análise da cobertura usando diamantes coloridos, conforme a imagem abaixo:

![Imagem5.png](images%2FImagem5.png)<br>
*Fonte: [Baeldung - Intro to JaCoCo](https://www.baeldung.com/jacoco)*

* **Diamante vermelho:** Indica que nenhum teste está cobrindo o branch;
* **Diamante amarelo:** Indica que o código está parcialmente coberto;
* **Diamante verde:** Indica que todo o branch foi testado e coberto;

---

## Visão geral sobre testes de API

### Contextualização

* **Testes de API** são testes que executa diretamente nos endpoints da aplicação, serve para validar comportamentos inesperados, evitando que usuários e aplicações desta API recebam resultados inesperados.
* Por meio dos testes é possível avaliar critérios da API como **funcionalidade**, **desempenho**, **confiabilidade** e **segurança**.
* Testes de API são um tipo de **teste de integração**.
* Lembrando, que os testes de integração são chamados de **testes em caixa preta**, ou seja, baseado nos requisitos, de forma que especificamos as entradas e saídas da nossa API, buscando cobrir os cenários importantes.

### MockMvc

* **MockMvc** fornece suporte para testar aplicação spring, encapsulando todos os beans necessários para testar a camada web.
* Amplamente usado para fazer o **teste de integração**.
* O teste de integração desempenha um papel importante no ciclo de vida de uma aplicação, verificando o **comportamento de ponta a ponta** de um sistema.

### Rest Assured

* **REST Assured** é uma biblioteca usada para automatizar testes de API em aplicações, permitindo testar e validar serviços REST de uma forma simples.

---

| Ferramentas / Anotações | Escopo do Contexto | Comportamento HTTP | Detalhes de Inicialização |
| :--- | :--- | :--- | :--- |
| **`@WebMvcTest` + MockMvc** | **Slice da camada web** (parte específica da aplicação de forma isolada) | Sem HTTP real | Sobe apenas a camada MVC (controllers, validações, filtros e handlers). Geralmente usa mocks para os services. |
| **`@SpringBootTest` + `@AutoConfigureMockMvc`** | **Contexto Spring completo** | Sem HTTP real | Sobe praticamente toda a aplicação. Usa MockMvc para simular requisições internamente sem abrir porta. |
| **`@SpringBootTest(webEnvironment)` + Rest Assured** | **Contexto Spring completo** | Via HTTP real | Sobe a aplicação com servidor embutido em uma porta real. Rest Assured faz requisições reais para localhost. |
