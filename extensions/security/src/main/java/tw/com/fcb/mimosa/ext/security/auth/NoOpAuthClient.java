package tw.com.fcb.mimosa.ext.security.auth;

class NoOpAuthClient implements AuthClient {

  @Override
  public ValidateResponse validateToken(ValidateRequest request) {
    throw new UnsupportedOperationException();
  }
}
