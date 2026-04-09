CREATE TABLE NFR.measurements (
    id                  BINARY(16) NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
    report_id           BINARY(16) NOT NULL,
    requirement_id      BINARY(16) NOT NULL,

    -- Quantitative / measurable targets (very important for NFRs)
    target_value        VARCHAR(50),                           -- "≤ 250 ms", "≥ 99.95%", "AES-256", "≤ 5 defects/kloc"
    threshold_value     VARCHAR(50),                           -- warning / soft limit
    unit                VARCHAR(25),                            -- "ms", "%", "concurrent users", "GB/month"
    measurement_method  VARCHAR(50),                           -- "average over 95th percentile", "SonarQube", "pen-test report"
    trigger             VARCHAR(50),
    context             VARCHAR(50),
    system_response     VARCHAR(50),
    owner               VARCHAR(50),
    acceptance_criteria VARCHAR(50),
    notes               VARCHAR(50),

    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    CONSTRAINT uq_measure_report_requirement UNIQUE (report_id, requirement_id),
    CONSTRAINT fk_measure_report_id  FOREIGN KEY (report_id)  REFERENCES reports(id),
    CONSTRAINT fk_measure_requirement_id  FOREIGN KEY (requirement_id)  REFERENCES requirements(nfr_id)
);
