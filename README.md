# nREPL Proxy

A simple proxy for debugging nREPL connections. It sits between your nREPL client and server, logging all messages exchanged.

## Installation

### With npm

```bash
npm install -g nreplproxy
```

### Single file executable

This cross-platform executable depends only on Node.js and doesn't require installing deps.

```bash
wget https://github.com/chr15m/nreplproxy/releases/latest/download/nreplproxy
chmod 755 nreplproxy
./nreplproxy
```

## Usage

Run `nreplproxy` in your project directory. It finds the `.nrepl-port` file, connects to the nREPL server, and then updates `.nrepl-port` so your client connects to the proxy. All nREPL messages will be logged to standard output. The original `.nrepl-port` file is restored on exit.

## Example log output

A brief example of the log output:

```
nREPL proxy started on port 46289
Proxying to nREPL server on port 34847
Client connected to proxy.

--- Client -> Server ---
{
  id: '5bbe1395-b048-47e2-24e6-678e9c14b84d',
  op: 'describe',
  'verbose?': 1
}

--- Server -> Client ---
{
  aux: { 'current-ns': 'shadow.user' },
  id: '5bbe1395-b048-47e2-24e6-678e9c14b84d',
  ops: {
    'add-middleware': {
      doc: 'Adding some middleware',
      optional: [Object],
      requires: [Object],
      returns: [Object]
    },
    clone: {
      doc: 'Clones the current session, returning the ID of the newly-created session.',
      optional: [Object],
      requires: {},
      returns: [Object]
    }
  },
...
```
