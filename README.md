# nREPL Proxy

A simple proxy for debugging nREPL connections. It sits between your nREPL client and server, logging all messages exchanged.

## Installation

### With npm

```bash
npm install -g nreplproxy
```

### Single file executable

Download the [cross-platform executable](https://github.com/chr15m/nreplproxy/releases/latest/download/nreplproxy) which depends only on Node.js and doesn't require installing deps.

```bash
wget https://github.com/chr15m/nreplproxy/releases/latest/download/nreplproxy
chmod 755 nreplproxy
./nreplproxy
```

## Usage

Run `nreplproxy` in your project directory. It finds the `.nrepl-port` file, connects to the nREPL server, and then updates `.nrepl-port` so your client connects to the proxy. All nREPL messages will be logged to standard output. The original `.nrepl-port` file is restored on exit.

## Example log output

```
nREPL proxy started on port 46289
Proxying to nREPL server on port 1339
Updating .nrepl-port from 1339 to 46289
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

--- Client -> Server ---
{
  code: '(inc 1)',
  column: 1,
  file: 'shadrepl/core.cljs',
  id: 'a38d7555-f5e4-41b4-a740-8f288b422e7b',
  line: 6,
  'nrepl.middleware.print/stream?': 1,
  ns: 'shadrepl.core',
  op: 'eval',
  session: 'a14147e2-599d-4dc8-801f-a76103a93947'
}

--- Server -> Client ---
{
  id: 'a38d7555-f5e4-41b4-a740-8f288b422e7b',
  session: 'a14147e2-599d-4dc8-801f-a76103a93947',
  value: '2'
}
```
