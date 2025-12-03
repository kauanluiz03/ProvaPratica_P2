module org.provap2estoque {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jakarta.persistence;

    requires org.hibernate.orm.core;
    requires java.sql;

    opens org.model to org.hibernate.orm.core, jakarta.persistence;

    opens org.provap2estoque to javafx.fxml, org.hibernate.orm.core, jakarta.persistence;

    exports org.provap2estoque;
}