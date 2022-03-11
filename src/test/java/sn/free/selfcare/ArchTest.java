package sn.free.selfcare;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("sn.free.selfcare");

        noClasses()
            .that()
                .resideInAnyPackage("sn.free.selfcare.service..")
            .or()
                .resideInAnyPackage("sn.free.selfcare.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..sn.free.selfcare.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
