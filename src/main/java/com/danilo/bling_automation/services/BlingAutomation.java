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
        System.out.println("Clonando produto...");
        cloneProduct(productData.get("type"), productData.get("bath"));

        System.out.println("Preenchendo detalhes do produto...");
        fillProductDetails(productData);

        System.out.println("Carregando imagem...");
        uploadImage(productData.get("sku"));

        System.out.println("Preenchendo detalhes do estoque...");
        fillStockDetails(productData);

        System.out.println("Salvando produto...");
        saveProduct();
    }

    private void cloneProduct(String type, String bath) {
        try {
            // Clique no botão de dropdown usando JavaScript
            String dropdownButtonXpath = "//button[@data-toggle='dropdown' and contains(@class, 'dropdown-toggle')]";
            System.out.println("Procurando o botão de dropdown com XPath: " + dropdownButtonXpath);
            WebElement dropdownButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dropdownButtonXpath)));
            scrollToElement(dropdownButton);
            clickUsingJavaScript(dropdownButton);

            // Clique na opção "Clonar produto" usando JavaScript
            String cloneProductOptionXpath = "//li/a/span[text()='Clonar produto']";
            System.out.println("Procurando a opção 'Clonar produto' com XPath: " + cloneProductOptionXpath);
            WebElement cloneProductOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(cloneProductOptionXpath)));
            scrollToElement(cloneProductOption);
            clickUsingJavaScript(cloneProductOption);
        } catch (Exception e) {
            System.out.println("Erro ao clonar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fillProductDetails(Map<String, String> productData) {
        WebElement skuField = waitAndFindElement(By.id("codigo"));
        clearAndSendKeys(skuField, productData.get("sku"));

        WebElement nameField = waitAndFindElement(By.id("nome"));
        clearAndSendKeys(nameField, productData.get("description"));

        WebElement priceField = waitAndFindElement(By.id("preco"));
        clearAndSendKeys(priceField, productData.get("salePrice"));

        // Sessão Características
        WebElement caracteristicasTab = waitAndFindElement(By.cssSelector("li[data-tab='div_caracteristicas']"));
        scrollToElement(caracteristicasTab);
        clickUsingJavaScript(caracteristicasTab);

        WebElement brandField = waitAndFindElement(By.id("marca"));
        clearAndSendKeys(brandField, "Bela Lure");

        // Descrição Curta
        driver.switchTo().frame("descricaoCurta_ifr");
        WebElement shortDescription = waitAndFindElement(By.id("tinymce"));
        clearAndSendKeys(shortDescription, productData.get("description") + "\n\n" +
                "Tamanho: " + productData.get("size") + "\n" +
                "Banho: Antialérgico em " + productData.get("bath") + "\n" +
                "Marca: Bela Lure\n" +
                "Coleção: " + productData.get("collection") + "\n\n");

        // Aplicando negrito
        Actions actions = new Actions(driver);
        String[] boldTexts = {
                "Garantia:\n",
                "Itens inclusos:\n",
                "Compra Garantida:\n",
                "Dicas para manter suas peças sempre lindas:\n"
        };

        String[] normalTexts = {
                "Esse acessório possui 1 ano de Garantia quanto banho, você recebe essa garantia junto com o seu pedido.\n\n",
                "- 1 " + getInclusos(productData.get("type")) + "\n- Embalagem para presente\n- Certificado de garantia\n\n",
                "Receba seu pedido no conforto da sua casa. Nós garantimos a entrega, se ocorrer qualquer problema com a entrega nós devolvemos o seu dinheiro.\n\n",
                "Evite uso em piscina, academia, contato com produtos químicos incluindo químicas para cabelo. Após o uso guardar as peças separadas uma das outras, evitando riscos e quebra de pedras.\n\nÓtima opção para presentear alguém especial! Acredite, você vai amar!!"
        };

        for (int i = 0; i < boldTexts.length; i++) {
            actions.moveToElement(shortDescription).sendKeys(boldTexts[i]).keyDown(Keys.CONTROL).sendKeys("b").keyUp(Keys.CONTROL).sendKeys(normalTexts[i]).perform();
        }

        driver.switchTo().defaultContent();

        // Descrição Complementar
        driver.switchTo().frame("descricaoComplementar_ifr");
        WebElement longDescription = waitAndFindElement(By.id("tinymce"));
        clearAndSendKeys(longDescription, productData.get("description"));
        driver.switchTo().defaultContent();

        // Campos Customizados
        WebElement customFieldBrand = waitAndFindElement(By.id("custom-field-1688250"));
        clearAndSendKeys(customFieldBrand, "Bela Lure");

        WebElement customFieldModel = waitAndFindElement(By.id("custom-field-1688254"));
        clearAndSendKeys(customFieldModel, productData.get("description"));
    }

    private void uploadImage(String sku) {
        try {
            // Acessa a aba de imagens
            WebElement imageTab = waitAndFindElement(By.cssSelector("li[data-tab='div_imagens']"));
            scrollToElement(imageTab);
            wait.until(ExpectedConditions.elementToBeClickable(imageTab));
            clickUsingJavaScript(imageTab);

            // Espera e clica para remover a imagem existente
            WebElement removeImage = waitAndFindElement(By.cssSelector("a[onclick='removerAnexoProduto()']"));
            scrollToElement(removeImage);
            clickUsingJavaScript(removeImage);

            // Espera e desmarca a caixa de seleção "Excluir arquivo do sistema"
            WebElement removeFileCheckbox = waitAndFindElement(By.id("removerArquivo"));
            if (removeFileCheckbox.isSelected()) {
                clickUsingJavaScript(removeFileCheckbox);
            }

            // Espera o campo de upload de imagem
            WebElement uploadField = waitAndFindElement(By.id("input_image_upload"));
            uploadField.sendKeys("C:/temp/projetos java/bling-automation/fotos/" + sku + ".jpg");

        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void fillStockDetails(Map<String, String> productData) {
        WebElement stockTab = waitAndFindElement(By.cssSelector("li[data-tab='div_estoque']"));
        scrollToElement(stockTab);
        clickUsingJavaScript(stockTab);

        WebElement quantityField = waitAndFindElement(By.id("estoqueSaldoQuantidade"));
        clearAndSendKeys(quantityField, productData.get("stock"));

        WebElement unitCostField = waitAndFindElement(By.id("estoqueSaldoPreco"));
        clearAndSendKeys(unitCostField, productData.get("costPrice"));

        WebElement totalCostField = waitAndFindElement(By.id("estoqueSaldoCusto"));
        clearAndSendKeys(totalCostField, productData.get("costPrice"));
    }

    private void saveProduct() {
        WebElement saveButton = waitAndFindElement(By.id("botaoSalvar"));
        scrollToElement(saveButton);
        clickUsingJavaScript(saveButton);
        System.out.println("Produto salvo com sucesso.");
    }

    private String getInclusos(String type) {
        switch (type) {
            case "Brinco Argola":
                return "Par de Argolas";
            case "Bracelete":
                return "Bracelete";
            case "Brinco Gota":
                return "Par de Brinco Gota";
            case "Anel":
                return "Anel";
            default:
                return "Produto";
        }
    }


    private WebElement waitAndFindElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void clearAndSendKeys(WebElement element, String text) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.clear();
        element.sendKeys(text);
    }


    private void clickUsingJavaScript(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }

    private void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }


    public void close() {
        driver.quit();
        System.out.println("Navegador fechado.");
    }
}
