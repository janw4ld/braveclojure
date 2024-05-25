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
    in {
      devShells.default = pkgs.mkShell {
        packages =
          [inputs.clj-nix.packages.${system}.deps-lock]
          ++ (with pkgs; [clojure clojure-lsp zprint]);
      };
    });
}
