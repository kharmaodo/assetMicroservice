package sn.free.selfcare.web.rest.errors;

public class OffreHasLigneException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public OffreHasLigneException() {
        super(ErrorConstants.OFFER_HAS_LINE, "Offer has already lines!", "offre", "offerhasline");
    }
}
