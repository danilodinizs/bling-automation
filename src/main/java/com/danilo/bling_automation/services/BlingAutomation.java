package com.danilo.bling_automation.services;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class BlingAutomation {

    private WebDriver driver;
    private WebDriverWait wait;

    public BlingAutomation() {
        // Configure o caminho para o executável do ChromeDriver
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/Chromedriver/chromedriver.exe"); // Atualize com o caminho correto
        this.driver = new ChromeDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Aumente o tempo limite para 30 segundos
    }

    public void createProduct(Map<String, String> productData) {
        startProductCreation();
        fillBasicDetails(productData);
        fillCharacteristics();
        fillShortDescription(productData);
        fillComplementaryDescription(productData.get("description"));
        fillCategory(productData.get("category"), productData);
        uploadImage(productData.get("sku"));
        fillStockDetails(productData);
        fillTaxDetails();
        //saveProduct();
    }

    private void startProductCreation() {
        try {
            // Clicar no botão "Incluir cadastro"
            WebElement incluirCadastroButton = waitAndFindElement(By.id("btn-incluir"));
            scrollToElement(incluirCadastroButton);
            clickUsingJavaScript(incluirCadastroButton);

            // Clicar no botão "Pular para versão completa"
            WebElement versaoCompletaButton = waitAndFindElement(By.id("novo_produto_pular"));
            scrollToElement(versaoCompletaButton);
            clickUsingJavaScript(versaoCompletaButton);
        } catch (Exception e) {
            System.out.println("Erro ao iniciar a criação do produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fillBasicDetails(Map<String, String> productData) {
        try {
            WebElement skuField = waitAndFindElement(By.id("codigo"));
            clearAndSendKeys(skuField, productData.get("sku"));

            WebElement nameField = waitAndFindElement(By.id("nome"));
            clearAndSendKeys(nameField, productData.get("description"));

            WebElement formatField = waitAndFindElement(By.id("formato"));
            selectOptionByText(formatField, "Simples");

            WebElement typeField = waitAndFindElement(By.id("tipo"));
            selectOptionByText(typeField, "Produto");

            WebElement salePriceField = waitAndFindElement(By.id("preco"));
            clearAndSendKeys(salePriceField, productData.get("salePrice"));

            WebElement unitField = waitAndFindElement(By.id("unidade"));
            selectOptionByText(unitField, "UN");

            WebElement conditionField = waitAndFindElement(By.id("condicao"));
            selectOptionByText(conditionField, "Novo");
        } catch (Exception e) {
            System.out.println("Erro ao preencher os detalhes básicos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fillCharacteristics() {
        try {
            WebElement brandField = waitAndFindElement(By.id("marca"));
            clearAndSendKeys(brandField, "Bela Lure");

            WebElement productionField = waitAndFindElement(By.id("producao"));
            clearAndSendKeys(productionField, "Própria");

            WebElement freeShippingField = waitAndFindElement(By.id("freteGratis"));
            selectOptionByText(freeShippingField, "Não");

            WebElement netWeightField = waitAndFindElement(By.id("pesoLiquido"));
            clearAndSendKeys(netWeightField, "0,150");

            WebElement grossWeightField = waitAndFindElement(By.id("pesoBruto"));
            clearAndSendKeys(grossWeightField, "0,150");

            WebElement widthField = waitAndFindElement(By.id("largura"));
            clearAndSendKeys(widthField, "14,00");

            WebElement heightField = waitAndFindElement(By.id("altura"));
            clearAndSendKeys(heightField, "5,00");

            WebElement depthField = waitAndFindElement(By.id("profundidade"));
            clearAndSendKeys(depthField, "17,00");

            WebElement volumesField = waitAndFindElement(By.id("volumes"));
            clearAndSendKeys(volumesField, "0");

            WebElement itemsPerBoxField = waitAndFindElement(By.id("itensPorCaixa"));
            clearAndSendKeys(itemsPerBoxField, "0,00");

            WebElement unitOfMeasurementField = waitAndFindElement(By.id("unidadeMedida"));
            selectOptionByText(unitOfMeasurementField, "centímetros");
        } catch (Exception e) {
            System.out.println("Erro ao preencher as características: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fillShortDescription(Map<String, String> productData) {
        try {
            driver.switchTo().frame("descricaoCurta_ifr");
            WebElement shortDescription = waitAndFindElement(By.id("tinymce"));
            String descriptionContent = productData.get("description") + "\n\n" +
                    "Tamanho: " + productData.get("size") + "\n" +
                    "Banho: Antialérgico em " + productData.get("bath") + "\n" +
                    "Marca: Bela Lure\n" +
                    "Coleção: " + productData.get("collection") + "\n\n" +
                    "Garantia:\n" +
                    "Esse acessório possui 1 ano de Garantia quanto banho, você recebe essa garantia junto com o seu pedido.\n\n" +
                    "Itens inclusos:\n" +
                    "- 1 " + getInclusos(productData.get("type")) + "\n- Embalagem para presente\n- Certificado de garantia\n\n" +
                    "Compra Garantida:\n" +
                    "Receba seu pedido no conforto da sua casa. Nós garantimos a entrega, se ocorrer qualquer problema com a entrega nós devolvemos o seu dinheiro.\n\n" +
                    "Dicas para manter suas peças sempre lindas:\n" +
                    "Evite uso em piscina, academia, contato com produtos químicos incluindo químicas para cabelo. Após o uso guardar as peças separadas uma das outras, evitando riscos e quebra de pedras.\n\n" +
                    "Ótima opção para presentear alguém especial! Acredite, você vai amar!!";
            clearAndSendKeys(shortDescription, descriptionContent);

            Actions actions = new Actions(driver);
            highlightText(actions, shortDescription, "Garantia:");
            highlightText(actions, shortDescription, "Itens inclusos:");
            highlightText(actions, shortDescription, "Compra Garantida:");
            highlightText(actions, shortDescription, "Dicas para manter suas peças sempre lindas:");

            driver.switchTo().defaultContent();
        } catch (Exception e) {
            System.out.println("Erro ao preencher a descrição curta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void highlightText(Actions actions, WebElement element, String textToHighlight) {
        actions.moveToElement(element).sendKeys(Keys.CONTROL, "f").sendKeys(textToHighlight).sendKeys(Keys.ENTER).keyDown(Keys.CONTROL).sendKeys("b").keyUp(Keys.CONTROL).perform();
    }

    private void fillComplementaryDescription(String description) {
        try {
            driver.switchTo().frame("descricaoComplementar_ifr");
            WebElement longDescription = waitAndFindElement(By.id("tinymce"));
            clearAndSendKeys(longDescription, description);
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            System.out.println("Erro ao preencher a descrição complementar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fillCategory(String category, Map<String, String> productData) {
        try {
            WebElement categoryField = waitAndFindElement(By.id("categoria"));
            selectOptionByText(categoryField, category);

            WebElement brandField = waitAndFindElement(By.id("custom-field-1688250"));
            clearAndSendKeys(brandField, "Bela Lure");

            WebElement materialField = waitAndFindElement(By.id("custom-field-1688251"));
            clearAndSendKeys(materialField, productData.get("bath"));

            WebElement brandNoBrandField = waitAndFindElement(By.id("custom-field-1688252"));
            clearAndSendKeys(brandNoBrandField, "");

            WebElement reasonField = waitAndFindElement(By.id("custom-field-1688253"));
            clearAndSendKeys(reasonField, "O produto não tem código cadastrado");
        } catch (Exception e) {
            System.out.println("Erro ao preencher a categoria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void uploadImage(String sku) {
        try {
            WebElement imageTab = waitAndFindElement(By.cssSelector("li[data-tab='div_imagens']"));
            scrollToElement(imageTab);
            clickUsingJavaScript(imageTab);

            WebElement removeImage = waitAndFindElement(By.cssSelector("a[onclick='removerAnexoProduto()']"));
            scrollToElement(removeImage);
            clickUsingJavaScript(removeImage);

            WebElement removeFileCheckbox = waitAndFindElement(By.id("removerArquivo"));
            if (removeFileCheckbox.isSelected()) {
                removeFileCheckbox.click();
            }
            WebElement uploadField = waitAndFindElement(By.id("input_image_upload"));
            uploadField.sendKeys("C:/temp/projetos java/bling-automation/fotos" + sku + ".jpg");
        } catch (Exception e) {
            System.out.println("Erro no upload da imagem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void fillStockDetails(Map<String, String> productData) {
        try {
            WebElement stockTab = waitAndFindElement(By.cssSelector("li[data-tab='div_estoque']"));
            scrollToElement(stockTab);
            clickUsingJavaScript(stockTab);

            WebElement quantityField = waitAndFindElement(By.id("estoqueSaldoQuantidade"));
            clearAndSendKeys(quantityField, productData.get("stock"));

            WebElement unitCostField = waitAndFindElement(By.id("estoqueSaldoPreco"));
            clearAndSendKeys(unitCostField, productData.get("costPrice"));

            WebElement totalCostField = waitAndFindElement(By.id("estoqueSaldoCusto"));
            clearAndSendKeys(totalCostField, productData.get("costPrice"));
        } catch (Exception e) {
            System.out.println("Erro ao preencher os detalhes de estoque: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void fillTaxDetails() {
        try {
            WebElement originField = waitAndFindElement(By.id("origem"));
            selectOptionByText(originField, "0 - Nacional, exceto as indicadas nos códigos 3, 4, 5 e 8");

            WebElement ncmField = waitAndFindElement(By.id("ncm"));
            clearAndSendKeys(ncmField, "7117.19.00");

            WebElement cestField = waitAndFindElement(By.id("cest"));
            clearAndSendKeys(cestField, "28.058.00");

            WebElement itemTypeField = waitAndFindElement(By.id("tipoItem"));
            selectOptionByText(itemTypeField, "Mercadoria para Revenda");

            WebElement tributesPercentageField = waitAndFindElement(By.id("percentualTributos"));
            clearAndSendKeys(tributesPercentageField, "0");

            WebElement icmsValueField = waitAndFindElement(By.id("valorIcms"));
            clearAndSendKeys(icmsValueField, "0,0000");

            WebElement ipiValueField = waitAndFindElement(By.id("valorIpi"));
            clearAndSendKeys(ipiValueField, "0");

            WebElement pisCofinsValueField = waitAndFindElement(By.id("valorPisCofins"));
            clearAndSendKeys(pisCofinsValueField, "0,0000");
        } catch (Exception e) {
            System.out.println("Erro ao preencher os detalhes de tributação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveProduct() {
        try {
            WebElement saveButton = waitAndFindElement(By.id("botaoSalvar"));
            scrollToElement(saveButton);
            clickUsingJavaScript(saveButton);
            System.out.println("Produto salvo com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao salvar o produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getInclusos(String type) {
        switch (type) {
            case "Brinco Argola":
                return "Par de Argolas";
            case "Bracelete":
                return "Bracelete";
            case "Brinco Gota":
                return "Par de Brincos Gota";
            case "Anel":
                return "Anel";
            default:
                return "Produto";
        }
    }

    private void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void clearAndSendKeys(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    private WebElement waitAndFindElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void clickUsingJavaScript(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }

    private void selectOptionByText(WebElement element, String text) {
        element.findElement(By.xpath("//option[text()='" + text + "']")).click();
    }

    public void close() {
        driver.quit();
        System.out.println("Navegador fechado.");
    }
}
