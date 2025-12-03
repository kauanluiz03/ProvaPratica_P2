module org.provap2estoque {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens org.provap2estoque to javafx.fxml;
    opens org.model to javafx.base, org.hibernate.orm.core, jakarta.persistence;

    exports org.provap2estoque;
    exports org.model;
}