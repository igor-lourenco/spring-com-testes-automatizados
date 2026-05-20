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
