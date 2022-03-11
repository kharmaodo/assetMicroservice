package sn.free.selfcare.web.rest.errors;

public class ProduitAlreadyAssignedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ProduitAlreadyAssignedException() {
        super(ErrorConstants.PRODUCT_ALREADY_ASSIGNED, "Product already assigned to a group!", "produit", "productassigned");
    }
}
