{
  description = "A clj-nix flake";
  nixConfig.bash-prompt-prefix = ''(clojure playground) '';

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    clj-nix = {
      url = "github:jlesquembre/clj-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = inputs:
    inputs.flake-utils.lib.eachDefaultSystem (system: let
      pkgs = inputs.nixpkgs.legacyPackages.${system};
      drv = inputs.clj-nix.lib.mkCljApp {
        inherit pkgs;
        modules = [
          {
            name = "janw4ld/braveclojure";
            main-ns = "hello.core";
            nativeImage.enable = true;
            projectSrc = with pkgs.lib.fileset;
              toSource {
                root = ./pkg;
                fileset = unions [
                  ./pkg/deps.edn
                  ./pkg/deps-lock.json
                  (fileFilter (file: file.hasExt "clj") ./pkg/src)
                ];
              };
          }
        ];
      };
    in {
      packages.default = drv;
      devShells.default = pkgs.mkShell {
        packages =
          [inputs.clj-nix.packages.${system}.deps-lock]
          ++ (with pkgs; [clojure clojure-lsp zprint]);
      };
    });
}
