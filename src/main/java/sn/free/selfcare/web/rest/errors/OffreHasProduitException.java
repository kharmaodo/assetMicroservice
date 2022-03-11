package sn.free.selfcare.web.rest.errors;

public class OffreHasProduitException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public OffreHasProduitException() {
        super(ErrorConstants.OFFER_HAS_PRODUCT, "Offer has already products!", "offre", "offerhasproduct");
    }
}
