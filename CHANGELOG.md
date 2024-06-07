# 0.4.0
> 07 Jun 2024

### Added
- `O200K` encoding

# 0.3.0
> 14 Oct 2023

Focused on internal refactoring and API stabilization to enhance usability and facilitate future development.

### Breaking Changes
- Rename `Enconding` to `EncodingConfig`.
- Rework encodings to enhance extensibility:
    - Add a new `Encoding` interface.
    - Introduce default implementations of `Encoding`: `CL100KBase`, `P50KBase`, `R50KBase`, and `P50KEdit`.
- Rename `Tokenizer.encoding` and `Tokenizer.encodingForModel` to `Tokenizer.of` with overrides.

# 0.2.0
> 12 Oct 2023

### Changed
- **JVM**: use local PBE loader by default
- **Encoding**: enable custom encoding

# 0.1.0
> 08 Oct 2023

Initial release.

### Added
- Encodings: `CL100K_BASE` , `R50K_BASE`, `P50K_BASE` and `P50K_EDIT`
- Custom encoding support
