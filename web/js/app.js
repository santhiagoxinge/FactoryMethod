/**
 * GlobalDocs Solutions - Interactive Dashboard Application Logic
 * Integrates directly with Java 25 Factory Method REST API
 */

document.addEventListener('DOMContentLoaded', () => {
    initNavigationTabs();
    initCountrySelectors();
    initPresets();
    initSingleDocumentForm();
    initBatchEngine();
    checkServerHealth();
});

// ==================== 1. NAVIGATION TABS ====================
function initNavigationTabs() {
    const tabs = document.querySelectorAll('.nav-tab');
    const panes = document.querySelectorAll('.tab-pane');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetId = tab.dataset.tab;
            tabs.forEach(t => t.classList.remove('active'));
            panes.forEach(p => p.classList.remove('active'));

            tab.classList.add('active');
            const targetPane = document.getElementById(targetId);
            if (targetPane) {
                targetPane.classList.add('active');
            }
        });
    });
}

// ==================== 2. COUNTRY SELECTION & DYNAMIC LABELS ====================
const COUNTRY_CONFIGS = {
    COLOMBIA: {
        name: 'Colombia',
        authority: 'DIAN',
        taxIdName: 'Fiscal ID (Colombia NIT)',
        taxIdPlaceholder: 'e.g., 900123456-1',
        defaultTaxId: '900123456-1',
        currency: 'COP',
        factoryName: 'ColombiaDocumentProcessorFactory'
    },
    MEXICO: {
        name: 'Mexico',
        authority: 'SAT',
        taxIdName: 'Fiscal ID (Mexico RFC)',
        taxIdPlaceholder: 'e.g., GDM180425ABC',
        defaultTaxId: 'GDM180425ABC',
        currency: 'MXN',
        factoryName: 'MexicoDocumentProcessorFactory'
    },
    ARGENTINA: {
        name: 'Argentina',
        authority: 'AFIP',
        taxIdName: 'Fiscal ID (Argentina CUIT)',
        taxIdPlaceholder: 'e.g., 30-12345678-9',
        defaultTaxId: '30-12345678-9',
        currency: 'ARS',
        factoryName: 'ArgentinaDocumentProcessorFactory'
    },
    CHILE: {
        name: 'Chile',
        authority: 'SII',
        taxIdName: 'Fiscal ID (Chile RUT)',
        taxIdPlaceholder: 'e.g., 12345678-K',
        defaultTaxId: '12345678-K',
        currency: 'CLP',
        factoryName: 'ChileDocumentProcessorFactory'
    }
};

function initCountrySelectors() {
    const radios = document.querySelectorAll('input[name="country"]');
    radios.forEach(radio => {
        radio.addEventListener('change', (e) => {
            document.querySelectorAll('.country-option').forEach(opt => opt.classList.remove('active'));
            radio.closest('.country-option').classList.add('active');
            updateCountryFormLabels(e.target.value);
        });
    });
}

function updateCountryFormLabels(countryCode) {
    const config = COUNTRY_CONFIGS[countryCode];
    if (!config) return;

    const taxIdLabel = document.getElementById('taxIdLabel');
    const amountLabel = document.getElementById('amountLabel');
    const taxInput = document.getElementById('taxIdentifier');

    if (taxIdLabel) taxIdLabel.textContent = config.taxIdName;
    if (amountLabel) amountLabel.textContent = `Amount (${config.currency})`;
    if (taxInput && !taxInput.dataset.userEdited) {
        taxInput.value = config.defaultTaxId;
    }
}

// ==================== 3. REALISTIC PRESETS ====================
const PRESETS = {
    'co-invoice': {
        country: 'COLOMBIA',
        docType: 'ELECTRONIC_INVOICE',
        format: 'pdf',
        title: 'Factura Electrónica de Venta Nacional No. FE-8921',
        taxId: '900123456-1',
        amount: 24500000,
        content: 'Venta de servicios cloud y arquitectura de microservicios con validación previa DIAN.'
    },
    'mx-contract': {
        country: 'MEXICO',
        docType: 'LEGAL_CONTRACT',
        format: 'docx',
        title: 'Contrato Marco de Servicios de Desarrollo Mercantil',
        taxId: 'GDM180425ABC',
        amount: 140000,
        content: 'Contrato sujeto a la jurisdicción de la Ciudad de México y constancia NOM-151 de conservación de mensajes de datos.'
    },
    'ar-report': {
        country: 'ARGENTINA',
        docType: 'FINANCIAL_REPORT',
        format: 'xlsx',
        title: 'Reporte Financiero Auditado Balances FACPCE RT54',
        taxId: '30-12345678-9',
        amount: 6200000,
        content: 'Estado contable de flujo de efectivo y balance general consolidado para presentación AFIP.'
    },
    'cl-cert': {
        country: 'CHILE',
        docType: 'DIGITAL_CERTIFICATE',
        format: 'pdf',
        title: 'Certificado de Firma Electrónica Avanzada DTE',
        taxId: '12345678-K',
        amount: 0,
        content: 'Certificado de autenticidad emitido por Prestador Acreditado para autorización y timbraje SII.'
    },
    'co-tax': {
        country: 'COLOMBIA',
        docType: 'TAX_DECLARATION',
        format: 'pdf',
        title: 'Declaración Renta y Complementarios Formulario 110',
        taxId: '900123456-1',
        amount: 88500000,
        content: 'Declaración anual personas jurídicas correspondiente al año gravable anterior.'
    },
    'invalid-rfc': {
        country: 'MEXICO',
        docType: 'ELECTRONIC_INVOICE',
        format: 'pdf',
        title: 'Factura con RFC Inválido de Prueba',
        taxId: 'BAD_RFC_9999',
        amount: 5000,
        content: 'Factura que violará las validaciones regulatorias de formato RFC exigidas por el SAT.'
    },
    'invalid-format': {
        country: 'COLOMBIA',
        docType: 'ELECTRONIC_INVOICE',
        format: 'txt',
        title: 'Factura en Formato TXT No Admitido',
        taxId: '900123456-1',
        amount: 12000,
        content: 'La DIAN no admite facturación electrónica en archivos planos TXT sin estructura regulada.'
    }
};

function initPresets() {
    const presetSelect = document.getElementById('presetSelect');
    if (!presetSelect) return;

    presetSelect.addEventListener('change', (e) => {
        const key = e.target.value;
        if (!key || !PRESETS[key]) return;

        const p = PRESETS[key];
        // Select country radio
        const radio = document.querySelector(`input[name="country"][value="${p.country}"]`);
        if (radio) {
            radio.checked = true;
            radio.dispatchEvent(new Event('change'));
        }

        document.getElementById('docTypeSelect').value = p.docType;
        document.getElementById('docFormatSelect').value = p.format;
        document.getElementById('docTitle').value = p.title;
        document.getElementById('taxIdentifier').value = p.taxId;
        document.getElementById('docAmount').value = p.amount;
        document.getElementById('docContent').value = p.content;

        // Reset preset selector
        presetSelect.value = '';
    });
}

// ==================== 4. SINGLE DOCUMENT SUBMISSION & VISUALIZATION ====================
function initSingleDocumentForm() {
    const form = document.getElementById('documentForm');
    const submitBtn = document.getElementById('btnSubmitDoc');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const country = document.querySelector('input[name="country"]:checked').value;
        const documentType = document.getElementById('docTypeSelect').value;
        const format = document.getElementById('docFormatSelect').value;
        const title = document.getElementById('docTitle').value;
        const taxIdentifier = document.getElementById('taxIdentifier').value;
        const amount = parseFloat(document.getElementById('docAmount').value) || 0;
        const content = document.getElementById('docContent').value;
        const customId = document.getElementById('docCustomId').value.trim();

        const payload = {
            documentId: customId || `DOC-${country.substring(0, 2)}-${Date.now()}`,
            title,
            country,
            documentType,
            format,
            taxIdentifier,
            amount,
            content
        };

        // UI Loading state
        submitBtn.disabled = true;
        submitBtn.innerHTML = `
            <svg class="spin-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 2a10 10 0 0 1 10 10"/></svg>
            Dispatching via Factory Method...
        `;

        try {
            const response = await fetch('/api/documents/process', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const result = await response.json();
            renderExecutionResult(result);
            highlightFactoryMethodNodes(country, result.processorUsed);
        } catch (err) {
            console.error('API Error:', err);
            renderErrorFallback(err.message, payload);
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = `
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
                Execute Factory Method Pipeline
            `;
        }
    });
}

function renderExecutionResult(result) {
    document.getElementById('idleStateNotice').classList.add('hidden');
    const activeCard = document.getElementById('activeResultCard');
    activeCard.classList.remove('hidden');

    const statusBadge = document.getElementById('resultStatusPill');
    statusBadge.className = 'status-badge';

    if (result.status === 'SUCCESS') {
        statusBadge.classList.add('status-success');
        statusBadge.textContent = 'COMPLIANT & CERTIFIED';
    } else if (result.status === 'WARNING') {
        statusBadge.classList.add('status-warning');
        statusBadge.textContent = 'COMPLIANT WITH WARNINGS';
    } else if (result.status === 'REJECTED') {
        statusBadge.classList.add('status-danger');
        statusBadge.textContent = 'REGULATORY REJECTION';
    } else {
        statusBadge.classList.add('status-danger');
        statusBadge.textContent = 'FAILED EXECUTION';
    }

    // Stat Cells
    document.getElementById('resFactory').textContent = result.factoryUsed || 'N/A';
    document.getElementById('resProcessor').textContent = result.processorUsed || 'N/A';
    document.getElementById('resDuration').textContent = `${result.processingTimeMs || 0} ms`;
    document.getElementById('resAuthority').textContent = `${result.taxAuthority || ''} (${result.countryDisplayName || result.country || ''})`;

    // Stamp
    document.getElementById('resStamp').textContent = result.regulatoryStamp || 'N/A';
    const stampLabel = document.getElementById('stampNameLabel');
    if (result.country === 'COLOMBIA') stampLabel.textContent = 'OFFICIAL REGULATORY STAMP (DIAN CUFE)';
    else if (result.country === 'MEXICO') stampLabel.textContent = 'OFFICIAL REGULATORY STAMP (SAT CFDI UUID)';
    else if (result.country === 'ARGENTINA') stampLabel.textContent = 'OFFICIAL REGULATORY STAMP (AFIP CAE)';
    else if (result.country === 'CHILE') stampLabel.textContent = 'OFFICIAL REGULATORY STAMP (SII DTE FOLIO)';
    else stampLabel.textContent = 'OFFICIAL REGULATORY STAMP';

    // Authority message
    document.getElementById('resAuthMessage').textContent = result.authorityValidationMessage || 'Validated';

    // Warnings & Errors
    const alertsBox = document.getElementById('resAlertsBox');
    alertsBox.innerHTML = '';
    const hasWarnings = result.warnings && result.warnings.length > 0;
    const hasErrors = result.errors && result.errors.length > 0;

    if (hasWarnings || hasErrors) {
        alertsBox.classList.remove('hidden');
        if (hasErrors) {
            result.errors.forEach(err => {
                const p = document.createElement('p');
                p.innerHTML = `<strong>Error:</strong> ${err}`;
                alertsBox.appendChild(p);
            });
        }
        if (hasWarnings) {
            result.warnings.forEach(w => {
                const p = document.createElement('p');
                p.innerHTML = `<strong>Notice:</strong> ${w}`;
                alertsBox.appendChild(p);
            });
        }
    } else {
        alertsBox.classList.add('hidden');
    }

    // Audit logs
    const auditList = document.getElementById('resAuditLogs');
    auditList.innerHTML = '';
    if (result.auditLogs && result.auditLogs.length > 0) {
        result.auditLogs.forEach(log => {
            const li = document.createElement('li');
            li.textContent = log;
            if (log.toLowerCase().includes('reject') || log.toLowerCase().includes('fail')) {
                li.className = 'fail';
            } else if (log.toLowerCase().includes('warning') || log.toLowerCase().includes('notice')) {
                li.className = 'warn';
            }
            auditList.appendChild(li);
        });
    } else {
        const li = document.createElement('li');
        li.textContent = 'Pipeline executed without granular audit events.';
        auditList.appendChild(li);
    }
}

function renderErrorFallback(errMsg, payload) {
    renderExecutionResult({
        documentId: payload.documentId,
        documentTitle: payload.title,
        country: payload.country,
        status: 'FAILED',
        regulatoryStamp: 'ERROR-UNREACHABLE',
        authorityValidationMessage: `Could not reach backend: ${errMsg}`,
        factoryUsed: 'CountryFactoryProvider',
        processorUsed: 'UnknownProcessor',
        processingTimeMs: 0,
        errors: [errMsg],
        auditLogs: ['Connection refused or network disruption. Ensure the Java server is running.']
    });
}

// Highlight nodes in the Factory Method visualizer
function highlightFactoryMethodNodes(country, processorName) {
    document.querySelectorAll('.diagram-node').forEach(n => n.style.borderColor = '');

    const factoryMap = {
        'COLOMBIA': 'nodeFactoryCO',
        'MEXICO': 'nodeFactoryMX',
        'ARGENTINA': 'nodeFactoryAR',
        'CHILE': 'nodeFactoryCL'
    };
    const factoryNodeId = factoryMap[country];
    if (factoryNodeId) {
        const node = document.getElementById(factoryNodeId);
        if (node) {
            node.style.borderColor = '#38bdf8';
            node.style.boxShadow = '0 0 20px rgba(56, 189, 248, 0.4)';
        }
    }

    const processorMap = {
        'ElectronicInvoiceProcessor': 'prodInvoice',
        'LegalContractProcessor': 'prodContract',
        'FinancialReportProcessor': 'prodReport',
        'DigitalCertificateProcessor': 'prodCertificate',
        'TaxDeclarationProcessor': 'prodTax'
    };
    const prodNodeId = processorMap[processorName];
    if (prodNodeId) {
        const node = document.getElementById(prodNodeId);
        if (node) {
            node.style.borderColor = '#38bdf8';
            node.style.boxShadow = '0 0 20px rgba(56, 189, 248, 0.4)';
        }
    }
}

// ==================== 5. BATCH PROCESSING ENGINE SIMULATION ====================
function initBatchEngine() {
    let currentBatchSize = 10;
    const batchButtons = document.querySelectorAll('.btn-toggle');
    const runBatchBtn = document.getElementById('btnRunBatch');

    batchButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            batchButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentBatchSize = parseInt(btn.dataset.count, 10);
        });
    });

    runBatchBtn.addEventListener('click', async () => {
        const multiCountry = document.getElementById('checkMultiCountry').checked;
        const injectErrors = document.getElementById('checkInjectErrors').checked;

        const documents = generateBatchDocuments(currentBatchSize, multiCountry, injectErrors);

        // Show progress UI
        const progressWrapper = document.getElementById('batchProgressWrapper');
        const progressBar = document.getElementById('batchProgressBar');
        const progressText = document.getElementById('batchProgressText');
        const progressPercent = document.getElementById('batchProgressPercent');
        const statsContainer = document.getElementById('batchStatsContainer');
        const tableContainer = document.getElementById('batchTableContainer');

        progressWrapper.classList.remove('hidden');
        statsContainer.classList.add('hidden');
        tableContainer.classList.add('hidden');
        progressBar.style.width = '15%';
        progressPercent.textContent = '15%';
        progressText.textContent = `Dispatching ${currentBatchSize} documents to thread pool...`;
        runBatchBtn.disabled = true;

        try {
            progressBar.style.width = '45%';
            progressPercent.textContent = '45%';

            const response = await fetch('/api/documents/batch', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(documents)
            });

            progressBar.style.width = '90%';
            progressPercent.textContent = '90%';

            const summary = await response.json();

            progressBar.style.width = '100%';
            progressPercent.textContent = '100%';
            progressText.textContent = `Batch ${summary.batchId} completed!`;

            renderBatchSummary(summary);
        } catch (err) {
            console.error('Batch Execution Error:', err);
            progressText.textContent = 'Batch execution error: ' + err.message;
        } finally {
            runBatchBtn.disabled = false;
        }
    });
}

function generateBatchDocuments(size, multiCountry, injectErrors) {
    const countries = ['COLOMBIA', 'MEXICO', 'ARGENTINA', 'CHILE'];
    const types = ['ELECTRONIC_INVOICE', 'LEGAL_CONTRACT', 'FINANCIAL_REPORT', 'DIGITAL_CERTIFICATE', 'TAX_DECLARATION'];
    const formats = ['pdf', 'docx', 'xlsx', 'csv'];

    const docs = [];
    for (let i = 1; i <= size; i++) {
        const cIndex = multiCountry ? (i % 4) : 0;
        const country = countries[cIndex];
        const type = types[i % types.length];
        const format = formats[i % formats.length];
        const cfg = COUNTRY_CONFIGS[country];

        let taxId = cfg.defaultTaxId;
        let finalFormat = format;
        let title = `Batch Item #${i} - ${type.replace('_', ' ')}`;

        // Test error injection
        if (injectErrors && (i % 7 === 0)) {
            if (i % 2 === 0) {
                taxId = 'CORRUPT_TAX_ID_99';
                title += ' [Invalid Tax ID Injected]';
            } else {
                finalFormat = 'txt'; // Illegal for invoices
                title += ' [Illegal Format Injected]';
            }
        }

        docs.push({
            documentId: `BATCH-${country.substring(0, 2)}-${1000 + i}`,
            title,
            country,
            documentType: type,
            format: finalFormat,
            taxIdentifier: taxId,
            amount: 10000 * (i + 1),
            content: `Batch ingestion payload item #${i} targeting ${country} regulatory authority.`
        });
    }
    return docs;
}

function renderBatchSummary(summary) {
    const statsContainer = document.getElementById('batchStatsContainer');
    const tableContainer = document.getElementById('batchTableContainer');
    statsContainer.classList.remove('hidden');
    tableContainer.classList.remove('hidden');

    document.getElementById('batchStatTotal').textContent = summary.totalDocuments;
    document.getElementById('batchStatSuccess').textContent = summary.successCount;
    document.getElementById('batchStatWarnings').textContent = summary.warningCount;
    document.getElementById('batchStatFailed').textContent = (summary.failedCount + summary.rejectedCount);
    document.getElementById('batchStatRate').textContent = `${summary.throughputDocsPerSecond} docs/s`;
    document.getElementById('batchStatDuration').textContent = `${summary.totalExecutionTimeMs} ms`;

    // Populate table
    const tbody = document.querySelector('#batchResultsTable tbody');
    tbody.innerHTML = '';

    if (summary.results) {
        summary.results.forEach(item => {
            const tr = document.createElement('tr');

            let badgeClass = 'status-success';
            if (item.status === 'WARNING') badgeClass = 'status-warning';
            else if (item.status === 'REJECTED' || item.status === 'FAILED') badgeClass = 'status-danger';

            tr.innerHTML = `
                <td class="font-mono">${item.documentId}</td>
                <td><strong>${item.country}</strong></td>
                <td>${item.documentTypeDisplay || item.documentType}</td>
                <td><span class="badge-mini">.${item.format}</span></td>
                <td><span class="status-badge ${badgeClass}">${item.status}</span></td>
                <td class="font-mono" style="max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${item.regulatoryStamp}">
                    ${item.regulatoryStamp || 'N/A'}
                </td>
                <td class="font-mono">${item.factoryUsed || 'N/A'}</td>
                <td class="font-mono">${item.processingTimeMs} ms</td>
            `;
            tbody.appendChild(tr);
        });
    }
}

// ==================== 6. SERVER HEALTH CHECK ====================
async function checkServerHealth() {
    const badge = document.getElementById('serverHealthBadge');
    try {
        const res = await fetch('/api/health');
        if (res.ok) {
            const data = await res.json();
            badge.textContent = `ONLINE (${data.freeMemoryMb} MB free)`;
            badge.style.color = '#38bdf8';
        }
    } catch (e) {
        badge.textContent = 'OFFLINE (Start Java)';
        badge.style.color = '#f87171';
    }
}
